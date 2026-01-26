# Users Feature - UI Design (Arsenije)

## Feature summary
This feature solves the problem of letting users authenticate and letting admins manage user accounts.

### Registration
- Page: `/register` → `register.html`
- Purpose: allow a new user to create an account
- Inputs: email/username, password, confirm password
- Output: success message + redirect to login, or inline errors

### Login / Logout
- Page: `/login` → `login.html`
- Purpose: authenticate existing users
- Inputs: username/email + password
- Output: on success redirect to home/orders, on failure show error
- Logout: available only when logged in

### Admin user management
- Page: `/admin/users` → `userAdmin.html`
- Purpose: allow ADMIN to view/edit/delete users
- Supporting pages:
    - `/admin/users/edit/{id}` → `editUser.html`
    - `/admin/users/delete/{id}` → `confirmDeleteUser.html`

## User roles
### Regular user
- Can login/logout
- Can use normal app pages (e.g., orders)
- Must NOT see admin links

### Admin
- All regular user abilities
- Can access admin user management pages
- Should see “User Admin” link in nav

## High-level user flows

### Flow 1: Register → Login → Use app
1. User visits `/register`
2. Submits form
3. If success: redirect to `/login` with “registration successful”
4. User logs in
5. Redirect to home/orders

### Flow 2: Admin → View users → Edit/Delete
1. Admin logs in
2. Visits `/admin/users`
3. Can click Edit → update details → save
4. Can click Delete → confirm delete → user removed

## UX error & feedback planning

### Registration errors (shown near top of card/form)
- Username/email already exists
- Password mismatch
- Missing required fields
- Backend provides: flags or model attributes (e.g. `error`, `message`, field errors)

### Login errors (shown near top of card/form)
- Invalid credentials
- Optional “Registration successful” message after redirect from register

### Recovery behavior
- Keep the user on the same page
- Preserve entered values where possible (except passwords)
- Make errors visible and readable
