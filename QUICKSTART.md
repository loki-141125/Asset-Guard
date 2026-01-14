# AssetGuard - Quick Start Guide

## ⚡ Quick Setup (5 minutes)

### Step 1: Database Setup
If using TiDB Cloud:
1. Create account at https://tidbcloud.com
2. Create a Serverless cluster
3. Copy connection details
4. Update `src/main/resources/application.properties`

If using local MySQL:
```sql
CREATE DATABASE assetguard_db;
USE assetguard_db;
```

Update properties:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/assetguard_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Step 2: Build & Run
```bash
cd d:\app
mvn clean install
mvn spring-boot:run
```

### Step 3: Access Application
- Home: http://localhost:8080
- Login: http://localhost:8080/login

### Step 4: Create Account
1. Click "Create Account" on home page
2. Fill registration form
3. Click "Sign In" after successful registration

## 👤 Test Credentials (After First Registration)
Use the account you just created during registration.

## 📊 First Steps in Dashboard

### 1. Add Your First Asset
- Click "Assets" in sidebar
- Click "+ Add Asset" button
- Fill in asset details:
  - Name (required)
  - Serial Number (required)
  - Category
  - Location
  - Price
  - Status (Available/In Use/Repair/Deprecated)
  - Assigned To
- Click "Save Asset"

### 2. View Dashboard
- Click "Dashboard" in sidebar
- View key statistics:
  - Total Assets
  - Available count
  - In Use count
  - Under Repair count
  - Total value

### 3. Track Maintenance
- Click "Maintenance" in sidebar
- Click "+ Add Record"
- Select asset
- Add maintenance details (type, description, cost, performer)
- Save record

### 4. View Activity Log
- Click "Audit Log" in sidebar
- See all actions performed
- Track who did what and when

## 🔧 Key Features Overview

### Asset Management
- Add, edit, delete assets
- Search and filter assets
- Track asset status
- Assign assets to people
- Track asset prices

### Maintenance Records
- Log maintenance activities
- Track maintenance costs
- Record service history
- Assign maintenance to personnel

### Organization
- Create departments
- Create asset categories
- Organize by location

### Reporting
- Real-time statistics
- Asset count by status
- Total asset value
- Activity logs

## 🔐 Security Tips

1. **Strong Password**: Use mix of uppercase, lowercase, numbers, symbols
2. **Don't Share**: Keep your login credentials private
3. **Logout**: Always logout when done
4. **Updates**: Keep your browser updated for security

## ❓ Common Tasks

### Add Multiple Assets
1. Go to Assets tab
2. Click "+ Add Asset" button
3. Fill details and save
4. Repeat for each asset

### Update Asset Status
1. Go to Assets tab
2. Find the asset in table
3. Click "Edit" button
4. Change status dropdown
5. Save changes

### Track Maintenance
1. Go to Maintenance tab
2. Click "+ Add Record"
3. Select asset
4. Record maintenance type and cost
5. Save

### Filter Assets
1. Go to Assets tab
2. Use status tabs (All, Available, In Use, Repair)
3. Or search by name/serial number

### View Activity
1. Go to Audit Log tab
2. See all system actions
3. Track who made changes and when

## 🚀 Tips & Tricks

- Use **Categories** to organize similar assets
- Track **Maintenance Costs** for budget planning
- Use **Departments** for multi-location tracking
- Check **Dashboard** regularly for statistics
- Review **Audit Log** for compliance

## 📞 Need Help?

1. Check the FAQ section
2. Review the comprehensive README
3. Check browser console (F12) for errors
4. Verify database connection in logs

## 🎓 Learning Path

1. **Beginner**: Create account, add assets
2. **Intermediate**: Organize with categories, track maintenance
3. **Advanced**: Analyze statistics, generate reports

---

Happy asset tracking! 🛡️

For detailed documentation, see README_ENHANCED.md
