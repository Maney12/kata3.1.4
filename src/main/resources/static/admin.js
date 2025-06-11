document.addEventListener('DOMContentLoaded', function () {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    const usersTableBody = document.getElementById('usersTableBody');
    const editUserModal = document.getElementById('editUserModal');
    const deleteUserModal = document.getElementById('deleteUserModal');

    const editUserForm = document.getElementById('editUserForm');
    const deleteUserForm = document.getElementById('deleteUserForm');
    const addUserForm = document.getElementById('addUserForm');

    const editRolesSelect = document.getElementById('editRoles');
    const addRolesSelect = document.getElementById('roles');
    const deleteRolesSelect = document.getElementById('deleteRoles');
    const addTab = document.getElementById('add-tab');

    let allRoles = [];

    function getSelectedRoles(selectElement, allRolesList) {
        return Array.from(selectElement.selectedOptions).map(option => {
            const foundRole = allRolesList.find(role => role.name === option.value);
            return {
                id: foundRole ? foundRole.id : null,
                name: option.value
            };
        }).filter(role => role.id !== null);
    }

    async function fetchAllUsers() {
        const response = await fetch('/api/admin/users');
        if (!response.ok) throw new Error(`Error fetching users: ${response.status}`);
        return await response.json();
    }

    async function fetchUserById(id) {
        const response = await fetch(`/api/admin/users/${id}`);
        if (!response.ok) throw new Error(`Error fetching user ${id}: ${response.status}`);
        return await response.json();
    }

    async function createUser(userData) {
        const response = await fetch('/api/admin/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(userData)
        });
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(`Error creating user: ${response.status} - ${errorBody}`);
        }
        return await response.json();
    }

    async function updateUser(id, userData) {
        const response = await fetch(`/api/admin/users/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(userData)
        });
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(`Error updating user ${id}: ${response.status} - ${errorBody}`);
        }
        return await response.json();
    }

    async function deleteUser(id) {
        const response = await fetch(`/api/admin/users/${id}`, {
            method: 'DELETE',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        });
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(`Error deleting user ${id}: ${response.status} - ${errorBody}`);
        }
    }

    async function fetchAllRoles() {
        const response = await fetch('/api/admin/roles');
        if (!response.ok) throw new Error(`Error fetching roles: ${response.status}`);
        return await response.json();
    }

    async function renderUsersTable() {
        try {
            const users = await fetchAllUsers();
            usersTableBody.innerHTML = '';
            users.forEach(user => {
                const row = usersTableBody.insertRow();
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.age}</td>
                    <td>${user.email}</td>
                    <td>${user.roles.map(role => role.name.replace('ROLE_', '')).join(', ')}</td>
                    <td>
                        <button class="btn btn-info btn-sm" data-bs-toggle="modal" data-bs-target="#editUserModal" data-user-id="${user.id}">Edit</button>
                    </td>
                    <td>
                        <button class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#deleteUserModal" data-user-id="${user.id}">Delete</button>
                    </td>
                `;
            });
        } catch (error) {
            console.error("Failed to render users table:", error);
        }
    }

    function populateRolesSelects(roles) {
        allRoles = roles;
        const selects = [editRolesSelect, addRolesSelect, deleteRolesSelect];
        selects.forEach(selectElement => {
            if (!selectElement) return;
            selectElement.innerHTML = '';
            allRoles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.name;
                option.textContent = role.name.replace('ROLE_', '');
                if (selectElement === deleteRolesSelect) {
                    option.disabled = true;
                }
                selectElement.appendChild(option);
            });
        });
    }

    editUserModal?.addEventListener('show.bs.modal', async function (event) {
        const button = event.relatedTarget;
        const userId = button.getAttribute('data-user-id');
        try {
            const user = await fetchUserById(userId);
            editUserForm.querySelector('#editUserId').value = user.id;
            editUserForm.querySelector('#editUserIdDisabled').value = user.id;
            editUserForm.querySelector('#editFirstName').value = user.firstName;
            editUserForm.querySelector('#editLastName').value = user.lastName;
            editUserForm.querySelector('#editAge').value = user.age;
            editUserForm.querySelector('#editEmail').value = user.email;
            editUserForm.querySelector('#editPassword').value = '';
            Array.from(editRolesSelect.options).forEach(option => option.selected = false);
            user.roles.forEach(userRole => {
                const option = Array.from(editRolesSelect.options).find(opt => opt.value === userRole.name);
                if (option) option.selected = true;
            });
        } catch (error) {
            console.error(`Error showing edit modal for user ${userId}:`, error);
        }
    });

    editUserForm?.addEventListener('submit', async function (event) {
        event.preventDefault();
        const userId = editUserForm.querySelector('#editUserId').value;
        const updatedUser = {
            id: userId,
            firstName: editUserForm.querySelector('#editFirstName').value,
            lastName: editUserForm.querySelector('#editLastName').value,
            age: parseInt(editUserForm.querySelector('#editAge').value, 10),
            email: editUserForm.querySelector('#editEmail').value,
            password: editUserForm.querySelector('#editPassword').value,
            roles: getSelectedRoles(editRolesSelect, allRoles)
        };
        try {
            await updateUser(userId, updatedUser);
            bootstrap.Modal.getInstance(editUserModal).hide();
            renderUsersTable();
        } catch (error) {
            console.error(`Failed to update user ${userId}:`, error);
        }
    });

    deleteUserModal?.addEventListener('show.bs.modal', async function (event) {
        const button = event.relatedTarget;
        const userId = button.getAttribute('data-user-id');
        deleteUserForm.querySelector('#deleteUserId').value = userId;
        try {
            const user = await fetchUserById(userId);
            deleteUserForm.querySelector('#deleteUserIdDisabled').value = user.id;
            deleteUserForm.querySelector('#deleteFirstName').value = user.firstName;
            deleteUserForm.querySelector('#deleteLastName').value = user.lastName;
            deleteUserForm.querySelector('#deleteAge').value = user.age;
            deleteUserForm.querySelector('#deleteEmail').value = user.email;
            deleteUserForm.querySelector('#deletePassword').value = '********';
            Array.from(deleteRolesSelect.options).forEach(option => option.selected = false);
            user.roles.forEach(userRole => {
                const option = Array.from(deleteRolesSelect.options).find(opt => opt.value === userRole.name);
                if (option) option.selected = true;
            });
        } catch (error) {
            console.error(`Error showing delete modal for user ${userId}:`, error);
        }
    });

    deleteUserForm?.addEventListener('submit', async function (event) {
        event.preventDefault();
        const userId = deleteUserForm.querySelector('#deleteUserId').value;
        try {
            await deleteUser(userId);
            bootstrap.Modal.getInstance(deleteUserModal).hide();
            renderUsersTable();
        } catch (error) {
            console.error(`Failed to delete user ${userId}:`, error);
        }
    });

    addTab?.addEventListener('shown.bs.tab', function () {
        addUserForm.reset();
        Array.from(addRolesSelect.options).forEach(option => option.selected = false);
        const defaultOption = Array.from(addRolesSelect.options).find(option => option.value === 'ROLE_USER');
        if (defaultOption) defaultOption.selected = true;
        const passwordField = addUserForm.querySelector('#password');
        if (passwordField) passwordField.value = '';
    });

    addUserForm?.addEventListener('submit', async function (event) {
        event.preventDefault();
        const newUser = {
            firstName: addUserForm.querySelector('#firstName').value,
            lastName: addUserForm.querySelector('#lastName').value,
            age: parseInt(addUserForm.querySelector('#age').value, 10),
            email: addUserForm.querySelector('#email').value,
            password: addUserForm.querySelector('#password').value,
            roles: getSelectedRoles(addRolesSelect, allRoles)
        };
        try {
            await createUser(newUser);
            renderUsersTable();

            const usersTableTab = document.getElementById('usersTableTab');
            const tab = new bootstrap.Tab(usersTableTab);
            tab.show();
        } catch (error) {
            console.error(`Failed to create user:`, error);
        }
    });


    async function initializePage() {
        try {
            const roles = await fetchAllRoles();
            populateRolesSelects(roles);
            renderUsersTable();
        } catch (error) {
            console.error("Failed to initialize page:", error);
        }
    }

    initializePage();
});
