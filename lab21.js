// Switch to the database lab21 (it will create if it doesn't exist)
db = db.getSiblingDB("lab21");

//create and populate drug collection
//drop if already exists
db.drug.drop();
db.drug.insertMany([
  { _id: 1, name: "lisinopril" },
  { _id: 2, name: "atorvastatin" },
  { _id: 3, name: "metformin" },
  { _id: 4, name: "amlodipine" },
  { _id: 5, name: "omeprazole" }
]);

//create and populate pharmacy collection
//drop if already exists
db.pharmacy.drop();
db.pharmacy.insertMany([
  {
    _id: 1,
    name: "CVS",
    address: "123 Main St",
    phone: "813-774-1200",
    drugCosts: [
      { drugName: "lisinopril", cost: 7.50 },
      { drugName: "atorvastatin", cost: 12.00 }
    ]
  },
  {
    _id: 2,
    name: "Walgreens",
    address: "456 Elm Ave",
    phone: "831-555-6789",
    drugCosts: [
      { drugName: "metformin", cost: 5.75 },
      { drugName: "amlodipine", cost: 6.20 },
      { drugName: "omeprazole", cost: 4.99 }
    ]
  }
]);

//confirm inserts via print
print("Inserted drugs:");
printjson(db.drug.find().toArray());

print("Inserted pharmacies:");
printjson(db.pharmacy.find().toArray());