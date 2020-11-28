package com.example.manufacturer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class RestServiceApplication implements CommandLineRunner {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public RestServiceApplication(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public static void main(String[] args) {
		SpringApplication.run(RestServiceApplication.class, args);

	}

	@Override
	public void run(final String... args) throws Exception {
		addHandleBarTypes();
		addMaterials();
		addGearShifts();
		addHandleMaterials();
		getRestriction();
	}

	private void addHandleBarTypes(){
		jdbcTemplate.execute("DROP TABLE HandleBarType IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE HandleBarType (handleBarType VARCHAR(255))");
		jdbcTemplate.update("INSERT INTO HandleBarType (handleBarType) VALUES (?)", "Flatbarlenker");
		jdbcTemplate.update("INSERT INTO HandleBarType (handleBarType) VALUES (?)", "Rennradlenker");
		jdbcTemplate.update("INSERT INTO HandleBarType (handleBarType) VALUES (?)", "Bullhornlenker");
	}

	private void addMaterials(){
		jdbcTemplate.execute("DROP TABLE Material IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE Material(material VARCHAR(255))");
		jdbcTemplate.update("INSERT INTO Material (material) VALUES (?)", "Aluminium");
		jdbcTemplate.update("INSERT INTO Material (material) VALUES (?)", "Stahl");
		jdbcTemplate.update("INSERT INTO Material (material) VALUES (?)", "Kunststoff");
	}

	private void addGearShifts(){
		jdbcTemplate.execute("DROP TABLE GearShift IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE GearShift(gearShift VARCHAR(255))");
		jdbcTemplate.update("INSERT INTO GearShift (gearShift) VALUES (?)", "Kettenschaltung");
		jdbcTemplate.update("INSERT INTO GearShift (gearShift) VALUES (?)", "Nabenschaltung");
		jdbcTemplate.update("INSERT INTO GearShift (gearShift) VALUES (?)", "Tretlagerschaltung");
	}

	private void addHandleMaterials(){
		jdbcTemplate.execute("DROP TABLE HandleMaterial IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE HandleMaterial(handleMaterial VARCHAR(255))");
		jdbcTemplate.update("INSERT INTO HandleMaterial (handleMaterial) VALUES (?)", "Ledergriff");
		jdbcTemplate.update("INSERT INTO HandleMaterial (handleMaterial) VALUES (?)", "Schaumstoffgriff");
		jdbcTemplate.update("INSERT INTO HandleMaterial (handleMaterial) VALUES (?)", "Kunststoffgriff");
	}

	private void getRestriction(){
		jdbcTemplate.execute("DROP TABLE Restriction IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE Restriction(restrictor VARCHAR(255), restrictedValue VARCHAR(255))");

		jdbcTemplate.update("INSERT INTO Restriction (restrictor, restrictedValue) VALUES (?,?)", "Flatbarlenker", "Aluminium");
		jdbcTemplate.update("INSERT INTO Restriction (restrictor, restrictedValue) VALUES (?,?)", "Flatbarlenker", "Kunststoff");
		jdbcTemplate.update("INSERT INTO Restriction (restrictor, restrictedValue) VALUES (?,?)", "Rennradlenker", "Aluminium");
		jdbcTemplate.update("INSERT INTO Restriction (restrictor, restrictedValue) VALUES (?,?)", "Rennradlenker", "Kunststoff");
		jdbcTemplate.update("INSERT INTO Restriction (restrictor, restrictedValue) VALUES (?,?)", "Stahl", "Kettenschaltung");
		jdbcTemplate.update("INSERT INTO Restriction (restrictor, restrictedValue) VALUES (?,?)", "Kunststoffgriff", "Kunststoff");
		jdbcTemplate.update("INSERT INTO Restriction (restrictor, restrictedValue) VALUES (?,?)", "Ledergriff", "Rennradlenker");
	}

}
