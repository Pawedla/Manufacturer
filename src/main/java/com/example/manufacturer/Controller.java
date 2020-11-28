package com.example.manufacturer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    public RMIClient client;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Controller(final JdbcTemplate jdbcTemplate) throws RemoteException, NotBoundException {
        this.jdbcTemplate = jdbcTemplate;
        client = new RMIClient();
        client.startClient();
    }

    @GetMapping("/getAvailableHandlebarTypes")
    public ResponseEntity getAvailableHandlebarTypes() throws ResponseStatusException {
        return new ResponseEntity(loadAvailableHandlebarTypes(), HttpStatus.ACCEPTED);
    }

    public ResponseEntity validateHandleBarTypeInput(String handleBarType) {
        return loadAvailableHandlebarTypes().contains(handleBarType) ?
                new ResponseEntity(HttpStatus.ACCEPTED) : generateNoSuchOption("HandleBarType", handleBarType);
    }

    @GetMapping("/getAvailableMaterial")
    public ResponseEntity getAvailableMaterial(@RequestParam final String handleBarType) throws ResponseStatusException {
        ResponseEntity validation = validateHandleBarTypeInput(handleBarType);
        return validation.getStatusCode().is4xxClientError() ?
                validation : new ResponseEntity(filterAvailableMaterial(handleBarType), HttpStatus.ACCEPTED);
    }

    public ResponseEntity validateMaterialInput(String material) {
        return loadAvailableMaterials().contains(material) ?
                new ResponseEntity(HttpStatus.ACCEPTED) : generateNoSuchOption("Material", material);
    }

    @GetMapping("/getHandleBarTypeMaterialValidation")
    public ResponseEntity getHandleBarTypeMaterialValidation(@RequestParam final String handleBarType, @RequestParam final String material) throws ResponseStatusException {
        if (validateHandleBarTypeInput(handleBarType).getStatusCode().is4xxClientError()) return validateHandleBarTypeInput(handleBarType);
        if (validateMaterialInput(material).getStatusCode().is4xxClientError()) return validateMaterialInput(material);
        List<String> restrictedMaterials = getRestrictedValues(handleBarType);
        return restrictedMaterials.contains(material) || restrictedMaterials.size() == 0 ?
                new ResponseEntity(HttpStatus.ACCEPTED) : generateNotCompatible(handleBarType, material);
    }

    @GetMapping("/getAvailableGearshifts")
    public ResponseEntity getAvailableGearshifts(@RequestParam String material) throws ResponseStatusException {
        ResponseEntity validation = validateMaterialInput(material);
        return validation.getStatusCode().is4xxClientError() ?
                validation : new ResponseEntity(filterAvailableGearshifts(material), HttpStatus.ACCEPTED);
    }

    public ResponseEntity validateGearShiftInput(String gearShift) {
        return loadAvailableGearShifts().contains(gearShift) ?
                new ResponseEntity(HttpStatus.ACCEPTED) : generateNoSuchOption("Gearshift", gearShift);
    }

    @GetMapping("/getMaterialGearshiftValidation")
    public ResponseEntity getMaterialGearshiftValidation(@RequestParam final String material, @RequestParam final String gearShift) throws ResponseStatusException {
        if (validateMaterialInput(material).getStatusCode().is4xxClientError()) return validateMaterialInput(material);
        if (validateGearShiftInput(gearShift).getStatusCode().is4xxClientError()) return validateGearShiftInput(gearShift);
        List<String> restrictedGearShifts = getRestrictedValues(material);
        return restrictedGearShifts.contains(gearShift) || restrictedGearShifts.size() == 0 ?
                new ResponseEntity(HttpStatus.ACCEPTED) : generateNotCompatible(material, gearShift);
    }

    @GetMapping("/getAvailableHandleMaterial")
    public ResponseEntity getAvailableHandleMaterial(@RequestParam String handleBarType, @RequestParam String material) throws ResponseStatusException {
        ResponseEntity validation = validateHandleBarTypeInput(handleBarType);
        if (validation.getStatusCode().is4xxClientError()) return validation;
        validation = validateMaterialInput(material);
        return validation.getStatusCode().is4xxClientError() ?
                validation : new ResponseEntity(filterAvailableHandleMaterial(handleBarType, material), HttpStatus.ACCEPTED);
    }

    public ResponseEntity validateHandleMaterialInput(String handleMaterial) {
        return loadAvailableHandleMaterial().contains(handleMaterial) ?
                new ResponseEntity(HttpStatus.ACCEPTED) : generateNoSuchOption("HandleMaterial", handleMaterial);
    }

    @GetMapping("/getHandleMaterialValidation")
    public ResponseEntity getHandleMaterialValidation(@RequestParam final String handleBarType, @RequestParam final String material, @RequestParam final String handleMaterial) throws ResponseStatusException {
        if (validateMaterialInput(material).getStatusCode().is4xxClientError()) return validateMaterialInput(material);
        if (validateHandleBarTypeInput(handleBarType).getStatusCode().is4xxClientError())
            return validateHandleBarTypeInput(handleBarType);
        if (validateHandleMaterialInput(handleMaterial).getStatusCode().is4xxClientError())
            return validateHandleMaterialInput(handleMaterial);
        List<String> restrictedHandleMaterials = getRestrictedValues(handleMaterial);
        if (restrictedHandleMaterials.size() == 0 || restrictedHandleMaterials.contains(material) || restrictedHandleMaterials.contains(handleBarType)) {
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else if (loadAvailableMaterials().contains(restrictedHandleMaterials.get(0))) {
            return generateNotCompatible(handleMaterial, material);
        } else return generateNotCompatible(handleMaterial, handleBarType);
    }

    private List<String> getRestrictedValues(String restrictor) {
        return jdbcTemplate.query("SELECT restrictedValue FROM Restriction WHERE restrictor = (?)",
                (rs, rowNum) -> ((rs.getString("restrictedValue"))), restrictor);
    }

    private List<String> getRestrictedHandleMaterials(String restrictor1, String restrictor2) {
        List<String> handleMaterials = loadAvailableHandleMaterial();
        Map<String, String> restrictedValues = new HashMap<>();
        List<String> restrictedHandleMaterials = new ArrayList<>();
        //getting all restrictions
        for (int i = 0; i < handleMaterials.size(); i++) {
            jdbcTemplate.query("SELECT restrictor, restrictedValue FROM Restriction WHERE restrictor = (?)",
                    (rs, rowNum) -> (restrictedValues.put((rs.getString("restrictor")), (rs.getString("restrictedValue")))), handleMaterials.get(i));
        }
        //removing restricted values from return value
        for (int i = 0; i < handleMaterials.size(); i++) {
            if (!restrictedValues.containsKey(handleMaterials.get(i))) {
                restrictedHandleMaterials.add(handleMaterials.get(i));
            }
        }
        //adding restricted values if restriction is satisfied
        for (String handleMaterial : handleMaterials) {
            System.out.println("handlecheck");
            if (restrictedValues.get(handleMaterial) != null && (restrictedValues.get(handleMaterial).equals(restrictor1) || restrictedValues.get(handleMaterial).equals(restrictor2))) {
                restrictedHandleMaterials.add(handleMaterial);
            }
        }
        return restrictedHandleMaterials;
    }

    @PostMapping("/order")
    public ResponseEntity<String> register(@RequestBody String[] order) {
        String supplier = SupplierContactUtil.getBestOffer(order);
        client.postBookOrder(supplier);
        return null;
    }

    private ResponseEntity generateNoSuchOption(String option, String selection) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST + " no such " + option + ": " + selection, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity generateNotCompatible(String selection1, String selection2) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST + " :" + selection1 + " is not compatible with " + selection2, HttpStatus.BAD_REQUEST);
    }

    private List<String> filterAvailableMaterial(String handleBarType) {
        List<String> materials = getRestrictedValues(handleBarType);
        return materials.size() == 0 ? loadAvailableMaterials() : materials;
    }

    private List<String> filterAvailableGearshifts(String material) {
        List<String> gearShifts = getRestrictedValues(material);
        return gearShifts.size() == 0 ? loadAvailableGearShifts() : gearShifts;
    }

    private List<String> filterAvailableHandleMaterial(String handleBarType, String material) {
        List<String> handleMaterial = getRestrictedHandleMaterials(handleBarType, material);
        return handleMaterial.size() == 0 ? loadAvailableHandleMaterial() : handleMaterial;
    }

    private List<String> loadAvailableHandlebarTypes() {
        return jdbcTemplate.query("SELECT handleBarType FROM HandleBarType", (rs, rowNum) -> (rs.getString("handleBarType")));
    }

    private List<String> loadAvailableMaterials() {
        return jdbcTemplate.query("SELECT material FROM material", (rs, rowNum) -> (rs.getString("material")));
    }

    private List<String> loadAvailableGearShifts() {
        return jdbcTemplate.query("SELECT gearShift FROM GearShift", (rs, rowNum) -> (rs.getString("gearShift")));
    }

    private List<String> loadAvailableHandleMaterial() {
        return jdbcTemplate.query("SELECT handleMaterial FROM HandleMaterial", (rs, rowNum) -> (rs.getString("handleMaterial")));
    }
}
