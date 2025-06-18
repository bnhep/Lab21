package application;

import application.model.*;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import view.PatientView;

@Controller
public class ControllerPatient {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SequenceService sequence;

    //Show registration form
    @GetMapping("/patient/new")
    public String getNewPatientForm(Model model) {
        model.addAttribute("patient", new PatientView());
        return "patient_register";
    }

    //Handle registration
    @PostMapping("/patient/new")
    public String createPatient(PatientView p, Model model) {
        Doctor d = doctorRepository.findByLastName(p.getPrimaryName());
        if (d == null) {
            model.addAttribute("message", "Doctor not found.");
            model.addAttribute("patient", p);
            return "patient_register";
        }

        int id = sequence.getNextSequence("PATIENT_SEQUENCE");

        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName(p.getFirstName());
        patient.setLastName(p.getLastName());
        patient.setBirthdate(p.getBirthdate());
        patient.setStreet(p.getStreet());
        patient.setCity(p.getCity());
        patient.setState(p.getState());
        patient.setZipcode(p.getZipcode());
        patient.setSsn(p.getSsn());
        patient.setPrimaryName(d.getLastName()); // link by name

        patientRepository.insert(patient);
        p.setId(id);
        model.addAttribute("message", "Registration successful.");
        model.addAttribute("patient", p);
        return "patient_show";
    }

    //Show search form
    @GetMapping("/patient/edit")
    public String getSearchForm(Model model) {
        model.addAttribute("patient", new PatientView());
        return "patient_get";
    }

    //Search for patient by ID and last name
    @PostMapping("/patient/show")
    public String showPatient(PatientView p, Model model) {
        Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());
        if (patient != null) {
            p.setFirstName(patient.getFirstName());
            p.setBirthdate(patient.getBirthdate());
            p.setStreet(patient.getStreet());
            p.setCity(patient.getCity());
            p.setState(patient.getState());
            p.setZipcode(patient.getZipcode());
            p.setSsn(patient.getSsn());
            p.setPrimaryName(patient.getPrimaryName());

            model.addAttribute("message", "Patient found.");
            model.addAttribute("patient", p);
            return "patient_show";
        } else {
            model.addAttribute("message", "Patient not found.");
            model.addAttribute("patient", p);
            return "patient_get";
        }
    }

    //Display editable form by ID (using name too, since no findById exists)
    @GetMapping("/patient/edit/{id}")
    public String getUpdateForm(@PathVariable int id, Model model) {
        // fallback strategy since you don’t have a findById-only method
        Patient patient = null;
        for (Patient p : patientRepository.findAll()) {
            if (p.getId() == id) {
                patient = p;
                break;
            }
        }

        if (patient == null) {
            model.addAttribute("message", "Patient not found.");
            return "index";
        }

        PatientView pv = new PatientView();
        pv.setId(patient.getId());
        pv.setFirstName(patient.getFirstName());
        pv.setLastName(patient.getLastName());
        pv.setBirthdate(patient.getBirthdate());
        pv.setStreet(patient.getStreet());
        pv.setCity(patient.getCity());
        pv.setState(patient.getState());
        pv.setZipcode(patient.getZipcode());
        pv.setSsn(patient.getSsn());
        pv.setPrimaryName(patient.getPrimaryName());

        model.addAttribute("patient", pv);
        return "patient_edit";
    }

    //Handle update form submission
    @PostMapping("/patient/edit")
    public String updatePatient(PatientView p, Model model) {
        Doctor d = doctorRepository.findByLastName(p.getPrimaryName());
        if (d == null) {
            model.addAttribute("message", "Doctor not found.");
            model.addAttribute("patient", p);
            return "patient_edit";
        }

        Patient patient = patientRepository.findByIdAndFirstNameAndLastName(
                p.getId(), p.getFirstName(), p.getLastName());

        if (patient == null) {
            model.addAttribute("message", "Patient not found.");
            model.addAttribute("patient", p);
            return "patient_edit";
        }

        patient.setStreet(p.getStreet());
        patient.setCity(p.getCity());
        patient.setState(p.getState());
        patient.setZipcode(p.getZipcode());
        patient.setPrimaryName(d.getLastName());

        patientRepository.save(patient);
        model.addAttribute("message", "Update successful.");
        model.addAttribute("patient", p);
        return "patient_show";
    }
}