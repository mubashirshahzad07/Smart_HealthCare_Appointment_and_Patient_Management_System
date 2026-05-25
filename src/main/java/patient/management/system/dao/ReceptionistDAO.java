package patient.management.system.dao;

import patient.management.system.dto.ReceptionistDTO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.model.Receptionist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("data/receptionists.json");

    public void addReceptionist(String username, String password, String name, Receptionist.Shift shift) {
        Receptionist newReceptionist = new Receptionist(username, password, name, shift);

        ArrayList<Receptionist> receptionists = getReceptionistsInternal();
        receptionists.add(newReceptionist);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, receptionists);

        } catch (IOException e) {
            throw new RuntimeException("Unable to register receptionist.");
        }
    }

    private ArrayList<Receptionist> getReceptionistsInternal() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Receptionist>>(){}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load receptionists data.");
        }
    }

    public List<ReceptionistDTO> getReceptionists() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<List<ReceptionistDTO>>(){}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load receptionists data.");
        }
    }
}
