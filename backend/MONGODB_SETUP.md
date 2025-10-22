# MongoDB Connection and Database Setup Guide

If you're not seeing the `blockchain_papers_db` database in MongoDB Compass, it's likely because MongoDB creates databases and collections lazily. This means the database won't appear until you've inserted data into it.

## Steps to Verify and Initialize MongoDB Connection

1. **Start your Spring Boot application** (if it's not already running)

2. **Access the test endpoint** to initialize the database:
   - Open your browser and navigate to: `http://localhost:8080/api/test/mongodb`
   - Or use curl: `curl http://localhost:8080/api/test/mongodb`

   This endpoint will create a test document in a collection called `test_collection` within the `blockchain_papers_db` database.

3. **Refresh MongoDB Compass**:
   - After accessing the test endpoint, refresh your MongoDB Compass view
   - You should now see the `blockchain_papers_db` database
   - Inside it, you'll find a `test_collection` with one document

## Creating an Admin User

To create a user with admin role, use the signup endpoint:

```
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
  "name": "Admin User",
  "email": "admin@example.com",
  "password": "password123",
  "institute": "Admin Institute",
  "subject": "System Administration",
  "role": "admin"
}
```

## Common Connection Issues

1. **MongoDB Not Running**: 
   - Ensure MongoDB is running on your machine
   - Default port is 27017

2. **Connection String Issues**: 
   - Check your application.properties file to ensure the MongoDB connection details are correct
   - Default is: localhost:27017

3. **Network/Firewall Issues**:
   - Ensure there are no firewalls blocking connections to MongoDB

## Database Collections

Once the application is actively used, you'll see these collections in your database:
- `users` - User accounts
- `papers` - Research paper submissions
- `blockchainRecords` - Blockchain verification records

## Manually Creating the Database (Alternative)

If you prefer, you can manually create the database in MongoDB Compass:
1. Click "Create Database"
2. Enter "blockchain_papers_db" as the database name
3. Enter "users" as the initial collection name
4. Click "Create"

However, this step is not necessary as Spring Data MongoDB will create the database automatically when data is first inserted.