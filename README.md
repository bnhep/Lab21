# Pharmacy Prescription Management Application – MongoDB

This project converts a prescription drug web application from the earlier relational-database/JDBC implementation to MongoDB. The application uses Spring Data MongoDB and the Model–View–Controller (MVC) pattern to manage doctors, patients, prescription drugs, pharmacies, prescriptions, prescription fills, and pharmacy-specific drug pricing.

The project was completed as a group assignment. The web application creates and manages doctor, patient, and prescription documents, while a `lab21.js` Mongo shell script creates and populates the pharmacy and drug collections with test data.

## Project Overview

The system supports healthcare and pharmacy operations by organizing related data across MongoDB collections:

- Doctors with their assigned patients
- Patients with their prescribing doctors
- Prescription documents with patients, doctors, and drugs
- Pharmacies with the drugs they stock and their prices
- Prescription-fill documents with pharmacies, dates, and medication costs

The application also supports validation scenarios such as registering patients, creating prescriptions, filling prescriptions, editing patient records, and displaying meaningful errors when invalid doctors, drugs, pharmacies, or prescription IDs are entered.

## MongoDB Data Model

The MongoDB database is organized around the following collections and document models:

- **doctor** – Stores provider identification, name, specialty, practice history, and SSN.
- **patient** – Stores patient identity, contact information, birthdate, SSN, address, and primary doctor name.
- **prescription** – Stores the drug, quantity, patient ID, doctor ID, creation date, refill count, and an embedded `fills` list. Filling a prescription adds a new fill entry to this list.
- **pharmacy** – Stores pharmacy information and an embedded `drugCosts` list containing available drugs and their prices.
- **drug** – Stores the names of valid prescription drugs.
- **database_sequences** – Stores the latest sequence value for doctors, patients, and prescriptions.

MongoDB document IDs and referenced identifiers connect related records across collections. Unlike the relational version, prescriptions contain their fill history and pharmacies contain their drug-cost data as embedded arrays. This reduces the need for relational joins and keeps closely related data within the documents that use it.

## How the Application Connects to MongoDB

The application connects to MongoDB through Spring Data MongoDB:

1. Each database document is represented by a Java model/entity class such as `Doctor`, `Patient`, `Prescription`, `Pharmacy`, or `Drug`.
2. Model classes use an ID field to map each object to MongoDB’s `_id` value. MongoDB can generate an `ObjectId`, or the application can assign an integer ID through its sequence service.
3. Each collection has a repository interface that extends `MongoRepository<Entity, IdType>`.
4. Spring creates the repository implementation automatically when the application starts.
5. Controllers receive repository instances through dependency injection using `@Autowired`.
6. Controllers use repository methods such as `findById`, `findAll`, `insert`, `save`, and `delete`, along with custom query methods defined by the repository interface.
7. The web application uses those repositories to create, retrieve, update, and delete doctor, patient, and prescription documents.

For example, a repository can define methods such as `findByName` or `findByAge`, and Spring Data converts the method names into MongoDB queries. The application’s MongoDB connection URI and database settings are supplied through the project’s Spring configuration.

A typical local Spring configuration for this project is:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=lab21
```

The URI connects to the local MongoDB server on the default port `27017`, while `spring.data.mongodb.database` selects the `lab21` database. The `CST363` label shown in MongoDB Compass is the saved connection name; it is not the database name. Connection credentials should be kept out of source control and supplied through local configuration or environment variables.

### SequenceService

MongoDB does not provide MySQL-style auto-increment integer keys by default. The application therefore uses `SequenceService` and `DatabaseSequence` to generate integer IDs for doctors, patients, and prescriptions. Each sequence is stored in the `database_sequences` collection, and the next value is generated atomically to avoid duplicate IDs when multiple requests occur concurrently.

## MVC Application Structure

- **Model:** `Doctor`, `Patient`, `Prescription`, `Pharmacy`, and `Drug` represent MongoDB documents.
- **View:** `PatientView`, `PrescriptionView`, and `Doctor` transfer data to and from the web templates.
- **Controller:** `ControllerDoctor`, `ControllerPatientCreate`, `ControllerPatientUpdate`, `ControllerPrescriptionCreate`, and `ControllerPrescriptionFill` process web requests and call the repositories.

This structure keeps user-interface handling in the controllers and templates while database operations are handled through the model classes and repository interfaces.

## Sample Data

The project includes sample documents for:

- Walgreens and CVS pharmacy locations
- Common prescription drugs such as lisinopril, loratadine, acetaminophen, lovastatin, Xanax, hydrocodone, and oxycodone
- Multiple package sizes and prices for each pharmacy

The sample pricing data demonstrates how the same medication may have different prices depending on the pharmacy and quantity purchased.

## MongoDB Setup

1. Install MongoDB Community Edition or start a MongoDB deployment.
2. Configure the Spring application with the local MongoDB connection URI and the `lab21` database name.
3. Run `lab21.js` with `mongosh` to create and populate the `drug` and `pharmacy` collections.
4. Start the Spring web application.
5. Use the application to create and populate doctor, patient, and prescription documents.
6. Run the documented registration, prescription, prescription-fill, and update workflows.

The application should use MongoDB document identifiers consistently when creating references between doctors, patients, prescriptions, and drugs. Prescription fills and pharmacy drug costs are stored inside their parent documents as embedded lists.

## Demonstrated Workflows

The project documents and validates the following application workflows:

1. Register a new patient with a valid doctor.
2. Reject patient registration when the doctor does not exist.
3. Create a prescription for a valid patient, doctor, and drug.
4. Reject a prescription containing an invalid drug.
5. Reject a prescription fill containing an invalid pharmacy.
6. Reject a prescription fill containing an invalid prescription ID.
7. Successfully fill a valid prescription.
8. Retrieve and update a patient profile.
9. Reject a patient update when the new doctor does not exist.

## MongoDB and Technical Skills Demonstrated

- NoSQL database design with MongoDB
- Collection and document modeling
- Spring Data MongoDB repositories
- Java entity classes mapped to MongoDB documents
- Repository-based CRUD operations and custom query methods
- Embedded document arrays for prescription fills and pharmacy drug costs
- Custom integer ID generation with a sequence service
- MongoDB document creation and sample-data population with `mongosh`
- Unique and supporting indexes
- Querying related healthcare and pharmacy records without relational joins
- Prescription and pharmacy workflow modeling
- Testing successful and unsuccessful application scenarios

## Team Project

This project was completed collaboratively by the Replicant Collective team. Team members contributed to the MongoDB data model, Spring MVC controllers, repository integration, application workflows, validation cases, and project documentation.

## Project Information

- Course: CST 363 – Database Systems
- Project: Lab 21 – MongoDB Pharmacy Web Application
- Database: MongoDB
- Database name: `lab21`
- Data initialization script: `lab21.js`
