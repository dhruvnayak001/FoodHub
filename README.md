# 🍔 FoodHub – Full Stack Food Ordering System

FoodHub is a full-stack web application that simulates a real-world food ordering platform. It allows users to browse menu items, manage their cart, place orders, and receive instant email confirmations with a PDF invoice.

---

## 🚀 Features

### 🖥️ Frontend
- Responsive and modern UI  
- Category-based menu filtering (Appetizers, Main Course, etc.)  
- Add to Cart with dynamic updates  
- Interactive cart with quantity controls  

### ⚙️ Backend (Spring Boot)
- RESTful APIs for menu, cart, and order management  
- Order processing with tax calculation  
- Seamless frontend-backend integration  

### 📩 Email & PDF System
- Automatic email confirmation after placing an order  
- PDF invoice generation with order details  
- Invoice sent as an email attachment  

---

## 🛠️ Tech Stack

**Frontend:**
- HTML  
- CSS  
- JavaScript  

**Backend:**
- Java (Spring Boot)  
- Spring MVC  

**Database:**
- MySQL  

**Other Tools:**
- SMTP (Email Integration)  
- PDF Generation (iText / OpenPDF)  

---

## 📂 Project Structure

```bash
FoodHub/
│
├── frontend/
│   ├── index.html
│   ├── styles.css
│   └── script.js
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── model/
```

---

## 🔄 Application Flow

1. User browses menu items  
2. Adds items to cart  
3. Places order  
4. Backend processes order  
5. PDF invoice is generated  
6. Email with invoice is sent to user  
