// API Configuration
// Detect if running locally or deployed
const API_BASE_URL = window.location.hostname === 'localhost'
    ? 'http://localhost:8080'                   // local backend
    : 'https://foodhub-7-backend.onrender.com'; // deployed backend

// Utility Functions
function showMessage(elementId, message, isError = false) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = message;
        element.className = isError ? 'message error' : 'message success';
        setTimeout(() => {
            element.className = 'message';
        }, 5000);
    }
}

function getAuthToken() {
    return localStorage.getItem('token');
}

function getUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

function setAuth(token, user) {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
}

function clearAuth() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('cart');
}

function isAuthenticated() {
    return !!getAuthToken();
}

function isAdmin() {
    const user = getUser();
    return user && user.role === 'ADMIN';
}

function checkAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'index.html';
    }
}

function checkAdminAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'index.html';
        return;
    }
    if (!isAdmin()) {
        window.location.href = 'menu.html';
    }
}

function logout() {
    clearAuth();
    window.location.href = 'index.html';
}

// API Helper
async function apiCall(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        },
        ...options
    };
    
    const token = getAuthToken();
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    try {
        const response = await fetch(url, config);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || data.message || 'Something went wrong');
        }
        
        return data;
    } catch (error) {
        throw error;
    }
}

// ========== AUTHENTICATION ==========
async function handleLogin(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    try {
        const data = await apiCall('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        
        setAuth(data.token, {
            id: data.id,
            email: data.email,
            fullName: data.fullName,
            role: data.role
        });
        
        if (data.role === 'ADMIN') {
            window.location.href = 'admin.html';
        } else {
            window.location.href = 'menu.html';
        }
    } catch (error) {
        showMessage('message', error.message, true);
    }
}

async function handleSignup(e) {
    e.preventDefault();
    
    const fullName = document.getElementById('fullName').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const address = document.getElementById('address').value;
    const password = document.getElementById('password').value;
    
    try {
        await apiCall('/auth/signup', {
            method: 'POST',
            body: JSON.stringify({ fullName, email, phone, address, password })
        });
        
        showMessage('message', 'Account created successfully! Redirecting to login...');
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 2000);
    } catch (error) {
        showMessage('message', error.message, true);
    }
}

// ========== MENU ==========
let currentCategory = 'all';

async function loadMenuItems() {
    try {
        let items;
        if (currentCategory === 'all') {
            items = await apiCall('/menu/items');
        } else {
            items = await apiCall(`/menu/items/${currentCategory}`);
        }
        
        displayMenuItems(items);
    } catch (error) {
        console.error('Error loading menu:', error);
    }
}

function displayMenuItems(items) {
    const grid = document.getElementById('menuGrid');
    if (!grid) return;
    
    grid.innerHTML = items.map(item => `
        <div class="menu-card">
            <img src="${item.imageUrl}" alt="${item.name}" onerror="this.src='https://via.placeholder.com/300x200?text=Food'">
            <div class="menu-card-content">
                <h3>${item.name}</h3>
                <p>${item.description}</p>
                <div class="menu-card-footer">
                    <span class="price">$${item.price.toFixed(2)}</span>
                    <button class="add-to-cart" onclick="addToCart(${item.id}, '${item.name}', ${item.price}, '${item.imageUrl}')">
                        <i class="fas fa-plus"></i> Add
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

function filterCategory(category) {
    currentCategory = category;
    
    document.querySelectorAll('.category-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    loadMenuItems();
}

// ========== CART ==========
function getCart() {
    const cart = localStorage.getItem('cart');
    return cart ? JSON.parse(cart) : [];
}

function saveCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartCount();
}

function addToCart(id, name, price, imageUrl) {
    const cart = getCart();
    const existingItem = cart.find(item => item.id === id);
    
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({ id, name, price, imageUrl, quantity: 1 });
    }
    
    saveCart(cart);
    showMessage('message', `${name} added to cart!`);
}

function updateCartCount() {
    const cart = getCart();
    const count = cart.reduce((total, item) => total + item.quantity, 0);
    
    document.querySelectorAll('#cartCount').forEach(el => {
        el.textContent = count;
    });
}

function loadCart() {
    const cart = getCart();
    const cartItems = document.getElementById('cartItems');
    const emptyCart = document.getElementById('emptyCart');
    const cartContent = document.getElementById('cartContent');
    
    if (!cartItems) return;
    
    if (cart.length === 0) {
        if (cartContent) cartContent.style.display = 'none';
        if (emptyCart) emptyCart.style.display = 'block';
        return;
    }
    
    if (cartContent) cartContent.style.display = 'block';
    if (emptyCart) emptyCart.style.display = 'none';
    
    cartItems.innerHTML = cart.map((item, index) => `
        <div class="cart-item">
            <img src="${item.imageUrl}" alt="${item.name}" onerror="this.src='https://via.placeholder.com/100?text=Food'">
            <div class="cart-item-details">
                <h4>${item.name}</h4>
                <p>$${item.price.toFixed(2)} each</p>
            </div>
            <div class="quantity-control">
                <button onclick="updateQuantity(${index}, -1)">-</button>
                <span>${item.quantity}</span>
                <button onclick="updateQuantity(${index}, 1)">+</button>
            </div>
            <div class="cart-item-price">$${(item.price * item.quantity).toFixed(2)}</div>
            <span class="remove-item" onclick="removeFromCart(${index})">
                <i class="fas fa-trash"></i>
            </span>
        </div>
    `).join('');
    
    updateCartSummary();
}

function updateQuantity(index, change) {
    const cart = getCart();
    cart[index].quantity += change;
    
    if (cart[index].quantity <= 0) {
        cart.splice(index, 1);
    }
    
    saveCart(cart);
    loadCart();
}

function removeFromCart(index) {
    const cart = getCart();
    cart.splice(index, 1);
    saveCart(cart);
    loadCart();
}

function updateCartSummary() {
    const cart = getCart();
    const subtotal = cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    const tax = subtotal * 0.1;
    const total = subtotal + tax;
    
    const subtotalEl = document.getElementById('subtotal');
    const taxEl = document.getElementById('tax');
    const totalEl = document.getElementById('total');
    
    if (subtotalEl) subtotalEl.textContent = `$${subtotal.toFixed(2)}`;
    if (taxEl) taxEl.textContent = `$${tax.toFixed(2)}`;
    if (totalEl) totalEl.textContent = `$${total.toFixed(2)}`;
}

// ========== CHECKOUT ==========
function showCheckoutModal() {
    const modal = document.getElementById('checkoutModal');
    const user = getUser();
    
    if (modal) {
        document.getElementById('deliveryAddress').value = user?.address || '';
        document.getElementById('deliveryPhone').value = user?.phone || '';
        modal.classList.add('active');
    }
}

function closeCheckoutModal() {
    const modal = document.getElementById('checkoutModal');
    if (modal) {
        modal.classList.remove('active');
    }
}

async function handleCheckout(e) {
    e.preventDefault();
    
    const cart = getCart();
    if (cart.length === 0) {
        showMessage('checkoutMessage', 'Your cart is empty!', true);
        return;
    }
    
    const deliveryAddress = document.getElementById('deliveryAddress').value;
    const phone = document.getElementById('deliveryPhone').value;
    const notes = document.getElementById('orderNotes').value;
    
    const items = cart.map(item => ({
        menuItemId: item.id,
        quantity: item.quantity
    }));
    
    try {
        await apiCall('/orders', {
            method: 'POST',
            body: JSON.stringify({ deliveryAddress, phone, notes, items })
        });
        
        saveCart([]);
        showMessage('checkoutMessage', 'Order placed successfully!');
        
        setTimeout(() => {
            closeCheckoutModal();
            window.location.href = 'orders.html';
        }, 1500);
    } catch (error) {
        showMessage('checkoutMessage', error.message, true);
    }
}

// ========== ORDERS ==========
async function loadUserOrders() {
    try {
        const orders = await apiCall('/orders/my-orders');
        displayOrders(orders);
    } catch (error) {
        console.error('Error loading orders:', error);
    }
}

function displayOrders(orders) {
    const container = document.getElementById('ordersList');
    const noOrders = document.getElementById('noOrders');
    
    if (!container) return;
    
    if (orders.length === 0) {
        if (noOrders) noOrders.style.display = 'block';
        container.innerHTML = '';
        return;
    }
    
    if (noOrders) noOrders.style.display = 'none';
    
    container.innerHTML = orders.map(order => `
        <div class="order-card">
            <div class="order-header">
                <div class="order-info">
                    <h3>Order #${order.id}</h3>
                    <p>${new Date(order.orderDate).toLocaleDateString()} at ${new Date(order.orderDate).toLocaleTimeString()}</p>
                </div>
                <span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span>
            </div>
            <div class="order-items-list">
                ${order.orderItems.map(item => `
                    <div class="order-item-row">
                        <img src="${item.menuItemImage}" alt="${item.menuItemName}" onerror="this.src='https://via.placeholder.com/80?text=Food'">
                        <div class="order-item-info">
                            <h4>${item.menuItemName}</h4>
                            <p>Qty: ${item.quantity} × $${item.price.toFixed(2)}</p>
                        </div>
                    </div>
                `).join('')}
            </div>
            <div class="order-footer">
                <span class="order-total">Total: $${order.totalAmount.toFixed(2)}</span>
            </div>
        </div>
    `).join('');
}

// ========== ADMIN ==========
function showTab(tab) {
    document.querySelectorAll('.admin-tab').forEach(t => t.classList.remove('active'));
    event.target.classList.add('active');
    
    const menuSection = document.getElementById('menuSection');
    const ordersSection = document.getElementById('ordersSection');
    
    if (tab === 'menu') {
        menuSection.style.display = 'block';
        ordersSection.style.display = 'none';
        loadAdminMenuItems();
    } else {
        menuSection.style.display = 'none';
        ordersSection.style.display = 'block';
        loadAllOrders();
    }
}

async function loadAdminMenuItems() {
    try {
        const items = await apiCall('/admin/menu');
        const tbody = document.getElementById('menuTableBody');
        
        if (!tbody) return;
        
        tbody.innerHTML = items.map(item => `
            <tr>
                <td><img src="${item.imageUrl}" alt="${item.name}" onerror="this.src='https://via.placeholder.com/60?text=Food'"></td>
                <td>${item.name}</td>
                <td>${item.category}</td>
                <td>$${item.price.toFixed(2)}</td>
                <td>
                    <span class="status-badge ${item.available ? 'status-ready' : 'status-cancelled'}">
                        ${item.available ? 'Available' : 'Unavailable'}
                    </span>
                </td>
                <td class="action-btns">
                    <button class="edit-btn" onclick="editMenuItem(${item.id}, '${item.name.replace(/'/g, "\\'")}', '${item.description.replace(/'/g, "\\'")}', ${item.price}, '${item.category}', '${item.imageUrl}', ${item.available})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="delete-btn" onclick="deleteMenuItem(${item.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading menu items:', error);
    }
}

function showAddMenuModal() {
    document.getElementById('modalTitle').textContent = 'Add Menu Item';
    document.getElementById('menuForm').reset();
    document.getElementById('menuItemId').value = '';
    document.getElementById('menuModal').classList.add('active');
}

function editMenuItem(id, name, description, price, category, imageUrl, available) {
    document.getElementById('modalTitle').textContent = 'Edit Menu Item';
    document.getElementById('menuItemId').value = id;
    document.getElementById('itemName').value = name;
    document.getElementById('itemDescription').value = description;
    document.getElementById('itemPrice').value = price;
    document.getElementById('itemCategory').value = category;
    document.getElementById('itemImage').value = imageUrl;
    document.getElementById('itemAvailable').checked = available;
    document.getElementById('menuModal').classList.add('active');
}

function closeMenuModal() {
    document.getElementById('menuModal').classList.remove('active');
}

async function handleMenuFormSubmit(e) {
    e.preventDefault();
    
    const id = document.getElementById('menuItemId').value;
    const itemData = {
        name: document.getElementById('itemName').value,
        description: document.getElementById('itemDescription').value,
        price: parseFloat(document.getElementById('itemPrice').value),
        category: document.getElementById('itemCategory').value,
        imageUrl: document.getElementById('itemImage').value,
        available: document.getElementById('itemAvailable').checked
    };
    
    try {
        if (id) {
            await apiCall(`/admin/menu/${id}`, {
                method: 'PUT',
                body: JSON.stringify(itemData)
            });
            showMessage('menuMessage', 'Menu item updated successfully!');
        } else {
            await apiCall('/admin/menu', {
                method: 'POST',
                body: JSON.stringify(itemData)
            });
            showMessage('menuMessage', 'Menu item added successfully!');
        }
        
        setTimeout(() => {
            closeMenuModal();
            loadAdminMenuItems();
        }, 1000);
    } catch (error) {
        showMessage('menuMessage', error.message, true);
    }
}

async function deleteMenuItem(id) {
    if (!confirm('Are you sure you want to delete this item?')) return;
    
    try {
        await apiCall(`/admin/menu/${id}`, {
            method: 'DELETE'
        });
        loadAdminMenuItems();
    } catch (error) {
        alert(error.message);
    }
}

async function loadAllOrders() {
    try {
        const orders = await apiCall('/admin/orders');
        const tbody = document.getElementById('ordersTableBody');
        
        if (!tbody) return;
        
        tbody.innerHTML = orders.map(order => `
            <tr>
                <td>#${order.id}</td>
                <td>User #${order.userId}</td>
                <td>${new Date(order.orderDate).toLocaleString()}</td>
                <td>$${order.totalAmount.toFixed(2)}</td>
                <td>
                    <span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span>
                </td>
                <td>
                    <select onchange="updateOrderStatus(${order.id}, this.value)" class="status-select">
                        <option value="PENDING" ${order.status === 'PENDING' ? 'selected' : ''}>PENDING</option>
                        <option value="PREPARING" ${order.status === 'PREPARING' ? 'selected' : ''}>PREPARING</option>
                        <option value="READY" ${order.status === 'READY' ? 'selected' : ''}>READY</option>
                        <option value="DELIVERED" ${order.status === 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                        <option value="CANCELLED" ${order.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                    </select>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading orders:', error);
    }
}

async function updateOrderStatus(orderId, status) {
    try {
        await apiCall(`/admin/orders/${orderId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
        alert('Order status updated successfully!');
        loadAllOrders();
    } catch (error) {
        alert(error.message);
    }
}

function displayOrders(orders) {
    const container = document.getElementById('ordersList');
    const noOrders = document.getElementById('noOrders');

    if (!container) return;

    if (orders.length === 0) {
        if (noOrders) noOrders.style.display = 'block';
        container.innerHTML = '';
        return;
    }

    if (noOrders) noOrders.style.display = 'none';

    container.innerHTML = orders.map(order => `
        <div class="order-card">
            <div class="order-header">
                <div class="order-info">
                    <h3>Order #${order.id}</h3>
                    <p>${new Date(order.orderDate).toLocaleDateString()} at ${new Date(order.orderDate).toLocaleTimeString()}</p>
                </div>
                <span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span>
            </div>
            <div class="order-items-list">
                ${order.orderItems.map(item => `
                    <div class="order-item-row">
                        <img src="${item.menuItemImage}" alt="${item.menuItemName}" onerror="this.src='https://via.placeholder.com/80?text=Food'">
                        <div class="order-item-info">
                            <h4>${item.menuItemName}</h4>
                            <p>Qty: ${item.quantity} × $${item.price.toFixed(2)}</p>
                        </div>
                    </div>
                `).join('')}
            </div>
            <div class="order-footer">
                <span class="order-total">Total: $${order.totalAmount.toFixed(2)}</span>
                ${order.status !== 'DELIVERED' && order.status !== 'CANCELLED'
                    ? `<button class="btn-cancel" onclick="cancelOrder(${order.id})">Cancel Order</button>`
                    : ''}
            </div>
        </div>
    `).join('');
}

async function cancelOrder(orderId) {
    if (!confirm('Are you sure you want to cancel this order?')) return;

    try {
        await apiCall(`/orders/${orderId}/cancel`, { method: 'PUT' });
        showMessage('message', 'Order cancelled successfully!');
        loadUserOrders(); // reload orders to reflect change
    } catch (error) {
        showMessage('message', error.message, true);
    }
}
