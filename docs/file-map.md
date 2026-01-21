# File Map (Backend Routes → Frontend Templates)

## Auth pages
GET  /login              -> login.html
GET  /register           -> register.html

## Admin user management
GET  /admin/users        -> userAdmin.html
GET  /admin/users/edit/{id}    -> editUser.html
GET  /admin/users/delete/{id}  -> confirmDeleteUser.html

## Shared UI fragments
(fragment) /fragments/navbar -> fragments/navbar.html

## Static assets
/static/app.css -> app.css
