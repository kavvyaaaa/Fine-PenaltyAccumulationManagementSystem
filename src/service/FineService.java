package service;

import dao.FineDAO;
import dao.ViolationDAO;
import model.Fine;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class FineService {

    private final FineDAO fineDAO;
    private final ViolationDAO violationDAO;

    public FineService() {
        this.fineDAO = new FineDAO();
        this.violationDAO = new ViolationDAO();
    }


    public List<Fine> getFinesForAccount(int accountId) throws SQLException {
        return fineDAO.getFinesByAccountId(accountId);
    }


    public List<Fine> getPendingFines(int accountId) throws SQLException {
        return fineDAO.getPendingFinesByAccountId(accountId);
    }


    public List<Fine> getAllFines() throws SQLException {
        return fineDAO.getAllFines();
    }


    public double getTotalFine(int accountId) throws SQLException {
        return fineDAO.getUserTotalFine(accountId);
    }


    public List<Object[]> getTotalFinePerUser() throws SQLException {
        return fineDAO.getTotalFinePerUser();
    }


    public double[] getAdminReport() throws SQLException {
        return fineDAO.getAdminReport();
    }


    public int issueFine(int accountId, int violationTypeId, String location,
                         int authorityId, double fineAmount, Date dueDate,
                         double penaltyPerDay) throws SQLException {
        int violationId = violationDAO.createViolation(accountId, violationTypeId, new Date(), location);
        if (violationId == -1) return -1;
        int fineId = fineDAO.createFine(violationId, authorityId, new Date(), dueDate, fineAmount);
        if (fineId == -1) return -1;
        if (penaltyPerDay > 0) {
            fineDAO.createPenalty(fineId, penaltyPerDay);
        }

        return fineId;
    }


    public List<Fine> getFinesByAuthority(int authorityId) throws SQLException {
        return fineDAO.getFinesByAuthorityId(authorityId);
    }
}
