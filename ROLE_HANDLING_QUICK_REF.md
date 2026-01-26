# Quick Reference: Spring Security Role Handling

## THE GOLDEN RULE
**Database stores roles WITHOUT prefix, Spring Security adds it**

---

## ROLE FORMAT CHEAT SHEET

| Location | Format | Example |
|----------|--------|---------|
| Database | No prefix | `ADMIN`, `USER` |
| CustomUserDetailsService (output) | With prefix | `ROLE_ADMIN`, `ROLE_USER` |
| SecurityConfig hasRole() | No prefix | `hasRole("ADMIN")` |
| SecurityConfig hasAuthority() | With prefix | `hasAuthority("ROLE_ADMIN")` |
| Thymeleaf sec:authorize | No prefix | `sec:authorize="hasRole('ADMIN')"` |

---

## CODE SNIPPETS

### Registration (AuthController)
```java
user.setRole("USER");  // ✅ No prefix
user.setEnabled(true);
```

### Loading User (CustomUserDetailsService)
```java
String role = user.getRole();  // From DB: "ADMIN"
if (!role.startsWith("ROLE_")) {
    role = "ROLE_" + role;  // Add prefix: "ROLE_ADMIN"
}
return new User(username, password, enabled, true, true, true,
    List.of(new SimpleGrantedAuthority(role)));
```

### Security Config
```java
.requestMatchers("/admin/**").hasRole("ADMIN")  // ✅ No prefix
// Spring Security converts to: hasAuthority("ROLE_ADMIN")
```

### HTML Template (editUser.html)
```html
<option value="USER">USER</option>     <!-- ✅ No prefix -->
<option value="ADMIN">ADMIN</option>   <!-- ✅ No prefix -->
```

---

## QUICK TEST

### Test Admin Access:
```bash
# 1. Login as admin
curl -X POST http://localhost:8081/login \
  -d "username=admin&password=password"

# 2. Access admin page (should work)
curl http://localhost:8081/admin/users
# Expected: 200 OK

# 3. Login as regular user
curl -X POST http://localhost:8081/login \
  -d "username=testuser&password=test123"

# 4. Access admin page (should fail)
curl http://localhost:8081/admin/users
# Expected: 403 Forbidden
```

---

## DATABASE VERIFICATION

```sql
-- Check roles (should NOT have ROLE_ prefix)
SELECT username, role FROM users;

-- Expected:
-- admin    | ADMIN
-- testuser | USER

-- NOT:
-- admin    | ROLE_ADMIN  ❌
-- testuser | ROLE_USER   ❌
```

---

## COMMON MISTAKES

❌ **Wrong**: Storing `ROLE_ADMIN` in database
✅ **Right**: Storing `ADMIN` in database

❌ **Wrong**: `hasRole("ROLE_ADMIN")` in SecurityConfig
✅ **Right**: `hasRole("ADMIN")` in SecurityConfig

❌ **Wrong**: Not adding prefix in CustomUserDetailsService
✅ **Right**: Adding `ROLE_` prefix when loading from DB

❌ **Wrong**: `<option value="ROLE_USER">USER</option>`
✅ **Right**: `<option value="USER">USER</option>`

---

## ROLE FLOW DIAGRAM

```
Registration
    ↓
Store "USER" in DB
    ↓
Login
    ↓
CustomUserDetailsService loads "USER"
    ↓
Adds prefix → "ROLE_USER"
    ↓
Spring Security stores authority "ROLE_USER"
    ↓
User accesses /admin/users
    ↓
SecurityConfig checks hasRole("ADMIN")
    ↓
Spring Security looks for "ROLE_ADMIN"
    ↓
User has "ROLE_USER" → 403 Forbidden ✅
Admin has "ROLE_ADMIN" → 200 OK ✅
```

---

## FILES TO CHECK

1. ✅ `AuthController.java` - Registration sets `USER`
2. ✅ `CustomUserDetailsService.java` - Adds `ROLE_` prefix
3. ✅ `SecurityConfig.java` - Uses `hasRole("ADMIN")`
4. ✅ `editUser.html` - Dropdown values are `USER`, `ADMIN`
5. ✅ `data.sql` - Initial admin has role `ADMIN`

---

## REMEMBER

- **hasRole()** = Spring adds `ROLE_` prefix automatically
- **hasAuthority()** = You must provide full authority name
- **Database** = Store without prefix
- **GrantedAuthority** = Store with prefix
