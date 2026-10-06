# Pharmacy Prescription Management Application

The Pharmacy Prescription Management Application is a web application backed by a MongoDB NoSQL database. It manages doctors, patients, prescription drugs, pharmacies, prescriptions, prescription fills, and pharmacy-specific drug pricing. The project models the complete prescription workflow from a doctor prescribing medication to a patient receiving a prescription fill from a pharmacy.

## Project Overview

The system supports healthcare and pharmacy operations by organizing related data across MongoDB collections:

- Doctors with their assigned patients
- Patients with their prescribing doctors
- Prescription documents with patients, doctors, and drugs
- Pharmacies with the drugs they stock and their prices
- Prescription-fill documents with pharmacies, dates, and medication costs

The application also supports validation scenarios such as registering patients, creating prescriptions, filling prescriptions, editing patient records, and displaying meaningful errors when invalid doctors, drugs, pharmacies, or prescription IDs are entered.

## MongoDB Data Model

The MongoDB database is organized around the following collections:

- **doctors** – Stores provider identification, name, specialty, practice history, and a unique SSN.
- **patients** – Stores patient identity, contact information, birthdate, SSN, and the assigned doctor reference.
- **drugs** – Stores the catalog of prescription drug names.
- **pharmacies** – Stores pharmacy names, addresses, and phone numbers.
- **prescriptions** – Stores the prescribing doctor reference, patient reference, drug reference, quantity, prescription date, and permitted refills.
- **prescriptionFills** – Records where and when a prescription was filled and the final fill price.
- **drugCosts** – Stores medication pricing by drug, pharmacy, and unit amount.

MongoDB document IDs and referenced identifiers connect related records across collections. Schema validation and unique indexes can be used to protect required fields, prevent duplicate SSNs, and support efficient lookups by doctor, patient, prescription, drug, and pharmacy. The document model also allows related pricing and fill details to evolve without requiring a rigid relational table structure.

## Sample Data

The project includes sample documents for:

- Walgreens and CVS pharmacy locations
- Common prescription drugs such as lisinopril, loratadine, acetaminophen, lovastatin, Xanax, hydrocodone, and oxycodone
- Multiple package sizes and prices for each pharmacy

The sample pricing data demonstrates how the same medication may have different prices depending on the pharmacy and quantity purchased.

## MongoDB Setup

1. Install MongoDB Community Edition or start a MongoDB deployment.
2. Create or select a database named `prescription`.
3. Load the sample drug, pharmacy, and drug-cost documents.
4. Configure the web application with the MongoDB connection URI.
5. Start the application and run the documented registration, prescription, and fill workflows.

The application should use MongoDB’s generated document identifiers or explicitly defined IDs consistently when creating references between doctors, patients, prescriptions, drugs, pharmacies, and prescription fills.

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
- Referenced relationships between related documents
- MongoDB document creation and sample-data population
- Unique and supporting indexes
- Schema validation for required fields and data formats
- Querying related healthcare and pharmacy records
- Prescription and pharmacy workflow modeling
- Testing successful and unsuccessful application scenarios

## Team Project

This project was completed collaboratively by the Replicant Collective team. Team members contributed to the MongoDB data model, application workflows, validation cases, and project documentation.

## Project Information

- Course: CST 363 – Database Systems
- Project: Lab 19 – Pharmacy Web Application
- Database: MongoDB
- Suggested database name: `prescription`
