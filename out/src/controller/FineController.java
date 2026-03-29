package controller;

import model.Fine;
import service.FineService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FineController {

    private final FineService fineService;

    public FineController() {
        this.fineService = new FineService();
    }


    public List<Fine> getFinesForAccount(int accountId) {
        try {
            return fineService.getFinesForAccount(accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public List<Fine> getPendingFines(int accountId) {
        try {
            return fineService.getPendingFines(accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public List<Fine> getAllFines() {
        try {
            return fineService.getAllFines();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public double getTotalFine(int accountId) {
        try {
            return fineService.getTotalFine(accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }


    public double[] getAdminReport() {
        try {
            return fineService.getAdminReport();
        } catch (SQLException e) {
            e.printStackTrace();
            return new double[]{0, 0, 0};
        }
    }


    public List<Object[]> getTotalFinePerUser() {
        try {
            return fineService.getTotalFinePerUser();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public int issueFine(int accountId, int violationTypeId, String location,
                         int authorityId, double fineAmount, Date dueDate,
                         double penaltyPerDay) {
        try {
            return fineService.issueFine(accountId, violationTypeId, location,
                    authorityId, fineAmount, dueDate, penaltyPerDay);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public List<Fine> getFinesByAuthority(int authorityId) {
        try {
            return fineService.getFinesByAuthority(authorityId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
