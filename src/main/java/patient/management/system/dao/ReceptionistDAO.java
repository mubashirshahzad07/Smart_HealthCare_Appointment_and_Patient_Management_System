package patient.management.system.dao;

import patient.management.system.dto.ReceptionistDTO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.model.Receptionist;
import patient.management.system.model.Role;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("data/receptionists.json");


    public void addReceptionist(String username, String password, String name, Receptionist.Shift shift) {
        ArrayList<Receptionist> receptionists = getActiveReceptionistsInternal();
        usernameAvailable(receptionists, username);

        Receptionist newReceptionist = new Receptionist(username, password, name, shift);
        new LoginDAO().addUser(newReceptionist.getUserId(), username, password, name, Role.RECEPTIONIST);
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

    public List<ReceptionistDTO> getAllReceptionists() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<ArrayList<ReceptionistDTO>>() {
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

        ArrayList<Receptionist> receptionists = getAllReceptionistsInternal();

        for (Receptionist receptionist : receptionists) {

            if (receptionist.getUserId().equals(id) || receptionist.getReceptionistId().equals(id)) {

                if (receptionist.getIsActive()) {
                    throw new RuntimeException("Receptionist is already active.");
                }

                receptionist.setIsActive(true);

                save(receptionists);

                return;
            }
        }

        throw new RuntimeException(
                "Receptionist not found.");
    }

    /**
     * @param id receptionistId or userId
     */
    public void inactivateReceptionist(String id) {

        ArrayList<Receptionist> receptionists = getAllReceptionistsInternal();

        for (Receptionist receptionist : receptionists) {

            if (receptionist.getUserId().equals(id) || receptionist.getReceptionistId().equals(id)) {

                if (!receptionist.getIsActive()) {
                    throw new RuntimeException("Receptionist is already inactive.");
                }

                receptionist.setIsActive(false);
                save(receptionists);

                return;
            }
        }

        throw new RuntimeException("Receptionist not found.");
    }

      //update
    public void updateReceptionistShift(String id, Receptionist.Shift newShift) {
    ArrayList<Receptionist> receptionists = getAllReceptionistsInternal();

    for (Receptionist receptionist : receptionists) {
        if (receptionist.getReceptionistId().equals(id) || receptionist.getUserId().equals(id)) {
            receptionist.setShift(newShift);
            save(receptionists);
            return;
        }
    }

    throw new RuntimeException("Receptionist not found.");
}

    public void usernameAvailable(ArrayList<Receptionist> receptionists, String username) {
        for (Receptionist receptionist : receptionists) {
            if (receptionist.getUsername().equals(username)) {
                throw new RuntimeException("Username not available.");
            }
        }
    }

    public Receptionist getReceptionistByUserId(String userId) {

        try {
            if (!file.exists() || file.length() == 0) {
                throw new RuntimeException("Receptionist not found.");
            }

            ArrayList<Receptionist> receptionists = mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Receptionist>>() {
                    });

            for (Receptionist receptionist : receptionists) {
                if (receptionist.getUserId().equals(userId)) {
                    return receptionist;
                }
            }

            throw new RuntimeException("Receptionist not found.");

        } catch (IOException e) {
            throw new RuntimeException("Unable to load receptionists data.");
        }
    }

    private void save(ArrayList<Receptionist> receptionists) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, receptionists);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save receptionists.");
        }
    }
}