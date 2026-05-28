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

        ArrayList<Receptionist> receptionists = getActiveReceptionistsInternal();
        receptionists.add(newReceptionist);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, receptionists);

        } catch (IOException e) {
            throw new RuntimeException("Unable to register receptionist.");
        }
    }

    private ArrayList<Receptionist> getActiveReceptionistsInternal() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            ArrayList<Receptionist> receptionists = mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Receptionist>>() {
                    });

            receptionists.removeIf(receptionist -> !receptionist.getIsActive());

            return receptionists;

        } catch (IOException e) {
            throw new RuntimeException("Unable to load receptionists data.");
        }
    }

    private ArrayList<Receptionist> getAllReceptionistsInternal() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Receptionist>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load receptionists data.");
        }
    }

    public List<ReceptionistDTO> getActiveReceptionists() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            ArrayList<ReceptionistDTO> receptionists = mapper.readValue(
                    file,
                    new TypeReference<ArrayList<ReceptionistDTO>>() {
                    });

            receptionists.removeIf(receptionist -> !receptionist.getIsActive());

            return receptionists;

        } catch (IOException e) {
            throw new RuntimeException("Unable to load receptionists data.");
        }
    }

    public List<ReceptionistDTO> getInactiveReceptionists() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            ArrayList<ReceptionistDTO> receptionists = mapper.readValue(
                    file,
                    new TypeReference<ArrayList<ReceptionistDTO>>() {
                    });

            receptionists.removeIf(receptionist -> receptionist.getIsActive());

            return receptionists;

        } catch (IOException e) {
            throw new RuntimeException("Unable to load receptionists data.");
        }
    }

    /**
     * @param id receptionistId or userId
     */
    public void activateReceptionist(String id) {
        ArrayList<Receptionist> activeReceptionists = getActiveReceptionistsInternal();

        for (Receptionist activeReceptionist : activeReceptionists) {
            if (activeReceptionist.getUserId().equals(id) || activeReceptionist.getReceptionistId().equals(id)) {
                activeReceptionist.setIsActive(true);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, activeReceptionists);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to activate receptionist.");
                }

            }
        }

        throw new RuntimeException("Receptionist is unregistered or active.");
    }

    /**
     * @param id receptionistId or userId
     */
    public void inactivateReceptionist(String id) {
        ArrayList<Receptionist> inactiveReceptionists = getActiveReceptionistsInternal();

        for (Receptionist inactiveReceptionist : inactiveReceptionists) {
            if (inactiveReceptionist.getUserId().equals(id) || inactiveReceptionist.getReceptionistId().equals(id)) {
                inactiveReceptionist.setIsActive(false);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, inactiveReceptionists);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to activate receptionist.");
                }

            }
        }

        throw new RuntimeException("Receptionist is unregistered or inactive.");
    }
}
