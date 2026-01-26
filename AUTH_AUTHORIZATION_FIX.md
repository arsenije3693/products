# Spring Security Authentication & Authorization Fix

## PROBLEM IDENTIFIED

**Role Prefix Mismatch** causing authorization failures:

### The Issue:
- Spring Security's `hasRole("ADMIN")` automatically adds `ROLE_` prefix
- If DB stores `ROLE_ADMIN`, Spring Security looks for `ROLE_ROLE_ADMIN` ❌
- This causes admin users to be denied access to admin routes

### The Root Cause:
```
DB stores: ROLE_ADMIN
Spring Security checks: hasRole("ADMIN") → looks for ROLE_ADMIN ✅
BUT if CustomUserDetailsService passes "ROLE_ADMIN" as-is,
Spring Security sees it already has prefix and adds another → ROLE_ROLE_ADMIN ❌
```

---

## SOLUTION

**Store roles WITHOUT `ROLE_` prefix in database, let Spring Security add it**

### Database Format:
- ✅ Store: `ADMIN`, `USER`
- ❌ Don't store: `ROLE_ADMIN`, `ROLE_USER`

### Spring Security:
- Uses `hasRole("ADMIN")` → automatically checks for `ROLE_ADMIN`
- CustomUserDetailsService adds `ROLE_` prefix when loading from DB

---

## FILES CHANGED

### 1. AuthController.java
**Location**: `src/main/java/edu/brajovic/products/controller/AuthController.java`

**Change**: Registration now stores role as `USER` (not `ROLE_USER`)

```java
// BEFORE:
user.setRole("ROLE_USER");

// AFTER:
user.setPassword(passwordEncoder.encode(user.getPassword()));
user.setRole("USER");
user.setEnabled(true);
```

**Why**: 
- Stores role without prefix in DB
- Explicitly sets enabled=true for new users
- Consistent with database schema expectations

---

### 2. CustomUserDetailsService.java
**Location**: `src/main/java/edu/brajovic/products/security/CustomUserDetailsService.java`

**Change**: Adds `ROLE_` prefix when loading user from DB

```java
// BEFORE:
return new org.springframework.security.core.userdetails.User(
    user.getUsername(),
    user.getPassword(),
    List.of(new SimpleGrantedAuthority(user.getRole()))
);

// AFTER:
public UserDetails loadUserByUsername(String username) {
    UserEntity user = userRepository.findByUsername(username);
    if (user == null) {
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    String role = user.getRole();
    if (!role.startsWith("ROLE_")) {
        role = "ROLE_" + role;
    }

    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            true, true, true,
            List.of(new SimpleGrantedAuthority(role))
    );
}
```

**Why**:
- Adds `ROLE_` prefix if not present (handles both formats)
- Checks `enabled` status from database
- Sets account/credentials/enabled flags properly
- Spring Security now sees `ROLE_ADMIN` and `ROLE_USER` correctly

---

### 3. editUser.html
**Location**: `src/main/resources/templates/editUser.html`

**Change**: Dropdown values use `USER` and `ADMIN` (not `ROLE_USER`, `ROLE_ADMIN`)

```html
<!-- BEFORE: -->
<select th:field="*{role}">
    <option value="ROLE_USER">USER</option>
    <option value="ROLE_ADMIN">ADMIN</option>
</select>

<!-- AFTER: -->
<select th:field="*{role}">
    <option value="USER">USER</option>
    <option value="ADMIN">ADMIN</option>
</select>
```

**Why**: 
- Matches database storage format
- Admin can edit user roles correctly
- Values stored in DB without prefix

---

### 4. SecurityConfig.java
**Location**: `src/main/java/edu/brajovic/products/security/SecurityConfig.java`

**No changes needed** - Already correct:
```java
.requestMatchers("/admin/**").hasRole("ADMIN")  // ✅ Correct
```

Spring Security automatically adds `ROLE_` prefix, so:
- `hasRole("ADMIN")` checks for `ROLE_ADMIN`
- `hasRole("USER")` checks for `ROLE_USER`

---

## ROLE FLOW EXPLANATION

### 1. Registration Flow
```
User registers → AuthController
  ↓
Sets role = "USER"
Sets password = BCrypt hash
Sets enabled = true
  ↓
Saves to DB: {username: "john", password: "$2a$...", role: "USER", enabled: true}
```

### 2. Login Flow
```
User enters username + password → Spring Security
  ↓
Calls CustomUserDetailsService.loadUserByUsername("john")
  ↓
Loads from DB: role = "USER"
  ↓
Adds prefix: role = "ROLE_USER"
  ↓
Returns UserDetails with GrantedAuthority("ROLE_USER")
  ↓
Spring Security authenticates and stores authorities
```

### 3. Authorization Flow
```
User accesses /admin/users → Spring Security
  ↓
Checks: hasRole("ADMIN")
  ↓
Looks for authority: "ROLE_ADMIN"
  ↓
User has: "ROLE_USER" → Access Denied (403)
Admin has: "ROLE_ADMIN" → Access Granted (200)
```

---

## DATABASE SCHEMA

### Current Schema (Correct):
```sql
CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username)
);
```

### Sample Data:
```sql
-- Admin user (password: "password")
INSERT INTO users (username, password, role, enabled)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZbN2/1lWz1QeYx2r1h3lqkQ5GQy1e', 'ADMIN', TRUE);

-- Regular user (created via registration)
-- username: "john", password: "password123", role: "USER", enabled: TRUE
```

---

## SECURITY CONFIGURATION SUMMARY

### Public Routes (No Authentication):
- `/login` - Login page
- `/register` - Registration page
- `/*.css`, `/css/**`, `/js/**`, `/images/**` - Static resources
- `/webjars/**`, `/favicon.ico` - Additional resources

### Protected Routes (Authentication Required):
- `/orders` - All authenticated users
- Any other route - All authenticated users

### Admin-Only Routes (ADMIN role required):
- `/admin/**` - All admin routes
  - `/admin/users` - User list
  - `/admin/users/edit/{id}` - Edit user
  - `/admin/users/delete/{id}` - Delete user confirmation

### Authentication:
- **Method**: Form-based login
- **Username Parameter**: `username` (default)
- **Password Parameter**: `password` (default)
- **Password Encoding**: BCrypt
- **Success URL**: `/orders` (forced redirect)
- **Logout URL**: `/logout` → redirects to `/login?logout`

---

## TEST CHECKLIST

### 1. Registration Tests
- [ ] Navigate to `http://localhost:8081/register`
- [ ] Register new user: username=`testuser`, password=`test123`
- [ ] Verify redirects to `/login`
- [ ] Check DB: role should be `USER` (not `ROLE_USER`)
- [ ] Check DB: enabled should be `TRUE`

### 2. Login Tests - Regular User
- [ ] Login with `testuser` / `test123`
- [ ] Should redirect to `/orders`
- [ ] Navigate to `http://localhost:8081/admin/users`
- [ ] Should get **403 Forbidden** (correct - not admin)

### 3. Login Tests - Admin User
- [ ] Login with `admin` / `password`
- [ ] Should redirect to `/orders`
- [ ] Navigate to `http://localhost:8081/admin/users`
- [ ] Should see **User Admin page** with user list (200 OK)
- [ ] Click "Edit" on a user
- [ ] Should load edit form with role dropdown showing USER/ADMIN
- [ ] Change role and save
- [ ] Should redirect back to user list

### 4. Authorization Tests
```
URL                              | Anonymous | USER Role | ADMIN Role
---------------------------------|-----------|-----------|------------
/login                          | 200 OK    | 200 OK    | 200 OK
/register                       | 200 OK    | 200 OK    | 200 OK
/orders                         | 302→login | 200 OK    | 200 OK
/admin/users                    | 302→login | 403       | 200 OK
/admin/users/edit/1             | 302→login | 403       | 200 OK
/admin/users/delete/1           | 302→login | 403       | 200 OK
```

### 5. Role Persistence Tests
- [ ] Admin edits user role from USER to ADMIN
- [ ] Check DB: role column should be `ADMIN` (not `ROLE_ADMIN`)
- [ ] Logout and login as that user
- [ ] User should now have access to `/admin/users`

### 6. Password Encoding Tests
- [ ] Register new user
- [ ] Check DB: password should start with `$2a$` (BCrypt)
- [ ] Login should work with plain text password
- [ ] Cannot login with BCrypt hash directly

---

## VERIFICATION QUERIES

### Check User Roles in Database:
```sql
SELECT id, username, role, enabled FROM users;
```

**Expected Output**:
```
id | username  | role  | enabled
---|-----------|-------|--------
1  | admin     | ADMIN | 1
2  | testuser  | USER  | 1
```

### Check Password Encoding:
```sql
SELECT username, LEFT(password, 10) as password_prefix FROM users;
```

**Expected Output**:
```
username  | password_prefix
----------|----------------
admin     | $2a$10$N9q
testuser  | $2a$10$...
```

---

## TROUBLESHOOTING

### Issue: Admin user gets 403 on /admin/users
**Check**:
1. Database role is `ADMIN` (not `ROLE_ADMIN`)
2. CustomUserDetailsService adds `ROLE_` prefix
3. SecurityConfig uses `hasRole("ADMIN")` (not `hasAuthority("ADMIN")`)

### Issue: New users can't login
**Check**:
1. User `enabled` field is `TRUE` in database
2. Password is BCrypt encoded (starts with `$2a$`)
3. Username exists in database

### Issue: Role changes don't take effect
**Solution**: User must logout and login again for role changes to apply

---

## KEY TAKEAWAYS

1. **Database stores**: `ADMIN`, `USER` (no prefix)
2. **Spring Security checks**: `hasRole("ADMIN")` → looks for `ROLE_ADMIN`
3. **CustomUserDetailsService**: Adds `ROLE_` prefix when loading from DB
4. **Registration**: Always assigns `USER` role, never allows role selection
5. **Admin**: Can edit user roles via `/admin/users/edit/{id}`
6. **Passwords**: Always BCrypt encoded before storage
7. **Enabled flag**: Must be `TRUE` for user to login

---

## SECURITY BEST PRACTICES IMPLEMENTED

✅ Passwords are BCrypt hashed (never stored plain text)
✅ Registration assigns USER role by default (principle of least privilege)
✅ Role-based access control (RBAC) properly configured
✅ Admin routes protected with ADMIN role requirement
✅ CSRF protection enabled (Spring Security default)
✅ Static resources publicly accessible (no auth required)
✅ Logout functionality properly configured
✅ Username uniqueness enforced at database level
✅ Account enabled/disabled functionality supported

---

## NEXT STEPS

1. **Rebuild**: `mvn clean package`
2. **Restart**: Stop and start the application
3. **Test**: Follow the test checklist above
4. **Verify**: Check database after registration/edits
5. **Monitor**: Check application logs for any authentication errors

If you encounter issues, check:
- Application logs for authentication failures
- Database for correct role values
- Browser network tab for 403 responses
- Spring Security debug logs (add `logging.level.org.springframework.security=DEBUG` to application.properties)
