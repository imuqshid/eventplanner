package cs3773.com.eventplanner.model;

import java.util.UUID;

/**
 * Created by Rodney on 3/26/2015.
 * <p/>
 * This class will hold information for employee accounts
 */
public class EmployeeAccount extends Account {

    public EmployeeAccount() {
    }

    public EmployeeAccount(UUID accountNumber, String username, String fullName, String email, String phoneNumber, Role role) {
        super(accountNumber, username, fullName, email, phoneNumber, role);
    }

    @Override
    public boolean saveAccount() {
        return false;
    }

    @Override
    public boolean deleteAccount() {
        return false;
    }

}
