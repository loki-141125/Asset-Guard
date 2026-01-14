# AssetGuard - Professional Asset Management System

A modern, feature-rich asset management solution for organizations to track, manage, and optimize their organizational assets with real-time insights and complete control.

## 🎯 Features

### Core Features
- **User Authentication & Registration**: Secure user registration and login with password encryption
- **Asset Management**: Complete CRUD operations for managing organizational assets
- **Real-time Dashboard**: Comprehensive statistics and asset overview
- **Maintenance Tracking**: Schedule and track maintenance records for assets
- **Audit Logging**: Complete audit trail of all system actions
- **Department Management**: Organize assets by departments
- **Asset Categories**: Classify assets with custom categories
- **Search & Filtering**: Find assets quickly with search and filter capabilities
- **Responsive Design**: Mobile-friendly interface for all devices

### Enhanced Features
- Asset pricing and value tracking
- Location tracking
- Asset assignment to users
- Maintenance cost tracking
- Role-based access control
- Real-time activity log
- Status tracking (Available, In Use, Repair, Deprecated)

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - ORM & Database
- **MySQL** - Database (TiDB Cloud compatible)
- **Maven** - Build tool

### Frontend
- **HTML5**
- **CSS3** (with modern features like Grid & Flexbox)
- **JavaScript (ES6+)**
- **Responsive Design**

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.8.5 or higher
- MySQL 5.7+ or TiDB Cloud account
- Modern web browser

## 🚀 Installation & Setup

### 1. Clone or Extract Project
```bash
cd d:/app
```

### 2. Configure Database

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://[HOST]:4000/[DATABASE]?sslMode=VERIFY_IDENTITY
spring.datasource.username=[USERNAME]
spring.datasource.password=[PASSWORD]
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

#### Using TiDB Cloud:
1. Create a TiDB Serverless cluster
2. Go to Connection section
3. Copy the connection string details
4. Update the properties file with your credentials

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or build and run the JAR:
```bash
mvn clean package
java -jar target/app-0.0.1-SNAPSHOT.jar
```

### 5. Access the Application

- **Home Page**: http://localhost:8080/
- **Login**: http://localhost:8080/login
- **Register**: http://localhost:8080/register
- **Dashboard**: http://localhost:8080/dashboard (after login)

## 📱 User Roles & Permissions

### User Role
- View all assets
- Create and manage assets
- Track maintenance
- View audit logs
- Manage personal department assets

### Admin Role (Future Enhancement)
- All user permissions
- User management
- Department management
- Category management
- System configuration

## 🎨 UI/UX Enhancements

### Modern Design Features
- **Gradient Color Scheme**: Professional purple gradient (667eea → 764ba2)
- **Responsive Layouts**: Grid and Flexbox for mobile optimization
- **Smooth Animations**: Hover effects and transitions
- **Intuitive Navigation**: Sidebar menu with clear sections
- **Status Badges**: Color-coded asset status indicators
- **Modal Forms**: Clean forms for adding/editing records
- **Real-time Search**: Live filtering of assets
- **Dashboard Cards**: Key metrics at a glance

## 📊 API Endpoints

### Authentication
- `POST /api/register` - Register new user
- `POST /api/login` - User login

### Assets
- `GET /api/assets` - Get all assets
- `GET /api/assets/{id}` - Get single asset
- `POST /api/assets` - Create new asset
- `PUT /api/assets/{id}` - Update asset
- `DELETE /api/assets/{id}` - Delete asset

### Statistics
- `GET /api/stats` - Get dashboard statistics

### History & Audit
- `GET /api/history` - Get audit logs

### Categories
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create category

### Departments
- `GET /api/departments` - Get all departments
- `POST /api/departments` - Create department

### Maintenance
- `GET /api/maintenance` - Get all maintenance records
- `POST /api/maintenance` - Create maintenance record

## 🔐 Security Features

- **Password Encryption**: BCrypt password encoding
- **SQL Injection Prevention**: Parameterized queries via JPA
- **CORS Configuration**: Controlled cross-origin requests
- **User Authentication**: Session-based authentication
- **Audit Logging**: Track all user actions

## 📦 Project Structure

```
app/
├── src/main/java/com/assetguard/app/
│   └── AssetGuardApplication.java (Main app + all entities, repos, controllers)
├── src/main/resources/
│   ├── application.properties
│   └── templates/
│       ├── index.html (Home page)
│       ├── login.html (Login page)
│       ├── register.html (Registration page)
│       └── dashboard.html (Main dashboard)
├── pom.xml (Maven dependencies)
└── Dockerfile (Container configuration)
```

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t assetguard:latest .
```

### Run Container
```bash
docker run -p 8080:8080 -e PORT=8080 assetguard:latest
```

## 📈 Future Enhancements

- [ ] Advanced reporting with PDF export
- [ ] Email notifications for maintenance
- [ ] Asset barcode/QR code scanning
- [ ] Mobile app
- [ ] API rate limiting
- [ ] Two-factor authentication
- [ ] Asset depreciation calculator
- [ ] Import/export functionality
- [ ] Webhook integrations
- [ ] Advanced search with filters

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License - see LICENSE.md for details

## 👨‍💻 Support

For support, email support@assetguard.com or create an issue in the repository.

## 🙋‍♂️ FAQ

**Q: How do I reset my password?**
A: Use the "Forgot password?" link on the login page (future enhancement)

**Q: Can I export asset data?**
A: Export functionality is coming in the next release

**Q: Is there a mobile app?**
A: Mobile app is in development

**Q: How secure is my data?**
A: We use industry-standard encryption and security practices. All passwords are hashed with BCrypt.

---

**AssetGuard** - Manage Your Assets with Confidence! 🛡️
