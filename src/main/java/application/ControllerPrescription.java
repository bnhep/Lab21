package application;

import application.model.*;
import application.model.DoctorRepository;
import application.model.PatientRepository;
import java.util.ArrayList;
import java.util.Optional;
import application.service.SequenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import application.model.Prescription;
import application.model.Prescription.FillRequest;
import application.model.PrescriptionRepository;
import view.PrescriptionView;

@Controller
public class ControllerPrescription {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private DrugRepository drugRepository;

  @Autowired
  private PrescriptionRepository prescriptionRepository;

  @Autowired
  private PharmacyRepository pharmacyRepository;

  @Autowired
  private SequenceService sequence;

  /*
   * Doctor requests blank form for new prescription.
   */
  @GetMapping("/prescription/new")
  public String getPrescriptionForm(Model model) {
    model.addAttribute("prescription", new PrescriptionView());
    return "prescription_create";
  }

  // process data entered on prescription_create form
  @PostMapping("/prescription")
  public String createPrescription(PrescriptionView p, Model model) {

    System.out.println("createPrescription " + p);

    /*
     * valid doctor name and id
     */
    //TODO
    Doctor d = doctorRepository.findByIdAndLastName(p.getDoctorId(), p.getDoctorLastName());
    if (d == null) {
      model.addAttribute("message", "Doctor not found. Check if "
          + "doctor id, first name and last name are correct.");
      model.addAttribute("prescription", p);
      return "prescription_create";
    }
    /*
     * valid patient name and id
     */
    Patient patient = patientRepository.findByIdAndLastName(p.getPatientId(),
        p.getPatientLastName());
    if (patient == null) {
      model.addAttribute("message", "Patient not found. Check if"
          + " patient id, first name and last name are correct.");
      model.addAttribute("prescription", p);
      return "prescription_create";
    }
    /*
     * valid drug name
     */
    Drug drug = drugRepository.findByName(p.getDrugName());
    if (drug == null) {
      model.addAttribute("message", "Drug not found. Check if"
          + " drug name is correct.");
      model.addAttribute("prescription", p);
      return "prescription_create";
    }
    /*
     * insert prescription
     */
    int id = sequence.getNextSequence("PRESCRIPTION_SEQUENCE");
    Prescription prescription = new Prescription();
    prescription.setRxid(id);
    prescription.setDrugName(p.getDrugName());
    prescription.setQuantity(p.getQuantity());
    prescription.setPatientId(p.getPatientId());
    prescription.setDoctorId(p.getDoctorId());
    prescription.setDateCreated(p.getDateCreated());
    prescription.setRefills(p.getRefills());

    //Initialize fills list
    prescription.setFills(new ArrayList<>());

    //Save the prescription
    prescriptionRepository.insert(prescription);

    p.setRxid(id);  // set generated id in view
    model.addAttribute("message", "Prescription created successfully.");
    model.addAttribute("prescription", p);
    return "prescription_show";
  }

  //Patient requests form to fill prescription.
  @GetMapping("/prescription/fill")
  public String getfillForm(Model model) {
    model.addAttribute("prescription", new PrescriptionView());
    return "prescription_fill";
  }

  //Handle submission of fill form (add fill request)
  // process data from prescription_fill form
  @PostMapping("/prescription/fill")
  public String processFillForm(PrescriptionView p, Model model) {
    System.out.println("processFillForm " + p);

    // Find pharmacy
    Pharmacy pharmacy = pharmacyRepository.findByNameAndAddress(p.getPharmacyName(),
        p.getPharmacyAddress());
    if (pharmacy == null) {
      model.addAttribute("message",
          "Pharmacy not found. Check if pharmacy name and address are correct.");
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }
    int pharmacyId = pharmacy.getId();
    String pharmacyPhone = pharmacy.getPhone();
    p.setPharmacyID(pharmacyId);
    p.setPharmacyPhone(pharmacyPhone);

    // Find prescription
    Prescription prescription = prescriptionRepository.findByRxid(p.getRxid());
    if (prescription == null) {
      model.addAttribute("message", "Prescription not found. Check if prescription ID is correct.");
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }

    // Find patient
    Patient patient = patientRepository.findByIdAndLastName(prescription.getPatientId(),
        p.getPatientLastName());
    if (patient == null) {
      model.addAttribute("message", "Patient not found or last name does not match prescription.");
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }

    int quantity = prescription.getQuantity();
    int allowedRefills = prescription.getRefills();
    int doctorId = prescription.getDoctorId();
    int patientId = patient.getId();
    String drugName = prescription.getDrugName();
    String datePrescribed = prescription.getDateCreated();
    String patientFirstName = patient.getFirstName();
    String patientLastName = patient.getLastName();

    // Verify patient last name matches
    if (!patientLastName.equalsIgnoreCase(p.getPatientLastName())) {
      model.addAttribute("message",
          "Patient name does not match prescription. Expected: " + patientLastName + ", but got: "
              + p.getPatientLastName());
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }

    p.setQuantity(quantity);
    p.setRefills(allowedRefills);
    p.setDoctorId(doctorId);
    p.setPatientId(patientId);
    p.setDrugName(drugName);
    p.setDateCreated(datePrescribed);
    p.setPatientFirstName(patientFirstName);
    p.setPatientLastName(patientLastName);

    // Check current fills
    int currentFills = prescription.getFills() != null ? prescription.getFills().size() : 0;
    if (currentFills >= allowedRefills + 1) { // +1 for original fill
      model.addAttribute("message",
          "Cannot fill prescription. Maximum number of refills (" + allowedRefills
              + ") has been exceeded.");
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }

    // Find doctor
    Doctor doctor = doctorRepository.findById(doctorId);
    if (doctor == null) {
      model.addAttribute("message", "Doctor not found for this prescription.");
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }
    p.setDoctorFirstName(doctor.getFirstName());
    p.setDoctorLastName(doctor.getLastName());

    // Calculate cost using Pharmacy.DrugCost
    ArrayList<Pharmacy.DrugCost> drugCosts = pharmacy.getDrugCosts();
    ArrayList<Pharmacy.DrugCost> matchingCosts = new ArrayList<>();
    for (Pharmacy.DrugCost dc : drugCosts) {
      if (dc.getDrugName().equalsIgnoreCase(drugName)) {
        matchingCosts.add(dc);
      }
    }
    if (matchingCosts.isEmpty()) {
      model.addAttribute("message", "Drug pricing not available at selected pharmacy.");
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }

    // Sort by unit size descending (assume drugName is like "30 mg" or "10 mg")
    matchingCosts.sort((a, b) -> {
      int unitA = 1, unitB = 1;
      try {
        unitA = Integer.parseInt(a.getDrugName().split(" ")[0]);
      } catch (Exception ignore) {
      }
      try {
        unitB = Integer.parseInt(b.getDrugName().split(" ")[0]);
      } catch (Exception ignore) {
      }
      return Integer.compare(unitB, unitA);
    });

    double totalCost = 0.0;
    int remainingQuantity = quantity;
    for (Pharmacy.DrugCost dc : matchingCosts) {
      int unitSize = 1;
      try {
        unitSize = Integer.parseInt(dc.getDrugName().split(" ")[0]);
      } catch (Exception ignore) {
      }
      if (remainingQuantity >= unitSize) {
        int unitsNeeded = remainingQuantity / unitSize;
        totalCost += unitsNeeded * dc.getCost();
        remainingQuantity = remainingQuantity % unitSize;
      }
    }
    // If any quantity remains, use the smallest available unit
    if (remainingQuantity > 0) {
      Pharmacy.DrugCost smallest = matchingCosts.get(matchingCosts.size() - 1);
      int unitSize = 1;
      try {
        unitSize = Integer.parseInt(smallest.getDrugName().split(" ")[0]);
      } catch (Exception ignore) {
      }
      double pricePerUnit = smallest.getCost() / unitSize;
      totalCost += remainingQuantity * pricePerUnit;
    }

    // Save fill
    Prescription.FillRequest fill = new Prescription.FillRequest();
    fill.setPharmacyID(pharmacyId);
    fill.setDateFilled(java.time.LocalDate.now().toString());
    fill.setCost(String.format("%.2f", totalCost));
    prescription.getFills().add(fill);
    prescriptionRepository.save(prescription);

    p.setCost(String.format("%.2f", totalCost));
    p.setDateFilled(fill.getDateFilled());
    p.setRefillsRemaining(allowedRefills - currentFills);

    model.addAttribute("message",
        "Prescription filled successfully. Cost: $" + String.format("%.2f", totalCost));
    model.addAttribute("prescription", p);
    return "prescription_show";
  }
}
