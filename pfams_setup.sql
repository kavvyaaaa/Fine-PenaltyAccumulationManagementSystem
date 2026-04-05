-- =========================================
-- PFAMS Database Setup Script
-- =========================================

DROP DATABASE IF EXISTS PFAMS;
CREATE DATABASE PFAMS;
USE PFAMS;

-- -----------------------------------------
-- 1. ROLE

CREATE TABLE Role (
    RoleID INT PRIMARY KEY AUTO_INCREMENT,
    RoleName VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO Role (RoleName) VALUES
('Admin'),
('User'),
('Authority');

-- -----------------------------------------
-- 2. USERS

CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Email VARCHAR(100) UNIQUE NOT NULL,
    Address VARCHAR(255),
    JoinDate DATE,
    PhoneNo VARCHAR(15)
);

INSERT INTO Users (FirstName, LastName, Email, Address, JoinDate, PhoneNo) VALUES
('Rahul','Sharma','rahul@gmail.com','Pune','2024-01-10','9876543210'),
('Amit','Patil','amit@gmail.com','Mumbai','2024-02-11','9876543211'),
('Sneha','Joshi','sneha@gmail.com','Nashik','2024-03-15','9876543212'),
('Neha','Kulkarni','neha@gmail.com','Pune','2024-04-12','9876543213'),
('Rohan','Deshmukh','rohan@gmail.com','Nagpur','2024-05-19','9876543214'),
('Priya','Mehta','priya@gmail.com','Delhi','2024-06-01','9876543215'),
('Karan','Shah','karan@gmail.com','Surat','2024-06-22','9876543216'),
('Anjali','Patel','anjali@gmail.com','Ahmedabad','2024-07-10','9876543217'),
('Vikas','Yadav','vikas@gmail.com','Noida','2024-08-05','9876543218'),
('Pooja','Singh','pooja@gmail.com','Lucknow','2024-08-20','9876543219'),
-- Admin user
('Admin','User','admin@pfams.com','System','2024-01-01','0000000000'),
-- Authority user
('Traffic','Officer','officer@pfams.com','Traffic Dept','2024-01-01','1111111111');

-- -----------------------------------------
-- 3. ACCOUNT (1:1 with USER)

CREATE TABLE Account (
    AccountID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT UNIQUE,
    Pass VARCHAR(100) NOT NULL,
    RoleID INT,
    TotalFine DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (RoleID) REFERENCES Role(RoleID)
);

INSERT INTO Account (UserID, Pass, RoleID) VALUES
-- User accounts (RoleID=2)
(1,'pass1',2),
(2,'pass2',2),
(3,'pass3',2),
(4,'pass4',2),
(5,'pass5',2),
(6,'pass6',2),
(7,'pass7',2),
(8,'pass8',2),
(9,'pass9',2),
(10,'pass10',2),
-- Admin account (RoleID=1)
(11,'admin',1),
-- Authority account (RoleID=3)
(12,'officer',3);

-- -----------------------------------------
-- 4. SEARCH REQUEST

CREATE TABLE SearchRequest (
    RequestID INT PRIMARY KEY AUTO_INCREMENT,
    AccountID INT,
    SearchText VARCHAR(255),
    SearchDate DATE,
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);

INSERT INTO SearchRequest (AccountID, SearchText, SearchDate) VALUES
(1,'No Helmet','2026-01-01'),
(2,'Over Speeding','2026-01-02'),
(3,'Signal Jump','2026-01-03'),
(4,'Wrong Parking','2026-01-04'),
(5,'Triple Riding','2026-01-05'),
(6,'No Seatbelt','2026-01-06'),
(7,'Expired Insurance','2026-01-07'),
(8,'Mobile Usage','2026-01-08'),
(9,'Without License','2026-01-09'),
(10,'Red Light Jump','2026-01-10');

-- -----------------------------------------
-- 5. VIOLATION TYPE

CREATE TABLE ViolationType (
    ViolationTypeID INT PRIMARY KEY AUTO_INCREMENT,
    VName VARCHAR(100),
    BaseFine DECIMAL(10,2) CHECK (BaseFine >= 0)
);

INSERT INTO ViolationType (VName, BaseFine) VALUES
-- Traffic related
('No Helmet',500),
('Over Speeding',1000),
('Signal Jump',700),
('Wrong Parking',300),
('No Seatbelt',400),
('Triple Riding',600),
('Mobile Usage While Driving',800),
('Expired Insurance',1200),
('Without License',1500),
-- College related
('Uniform Missing',200),
('ID Card Missing',150),
('Late Fee Submission',250),
('Library Book Late Return',100),
('Attendance Below 75%',0),
('Ragging Violation',5000),
('Lab Equipment Damage',800);

-- -----------------------------------------
-- 6. VIOLATION RECORDS

CREATE TABLE Violation (
    ViolationID INT PRIMARY KEY AUTO_INCREMENT,
    AccountID INT NOT NULL,
    ViolationTypeID INT,
    VDate DATE,
    Location VARCHAR(150),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE,
    FOREIGN KEY (ViolationTypeID) REFERENCES ViolationType(ViolationTypeID)
);

INSERT INTO Violation (AccountID, ViolationTypeID, VDate, Location) VALUES
(1,1,'2026-01-01','FC Road'),
(2,6,'2026-01-02','SIT Campus'),
(3,7,'2026-01-03','Main Gate'),
(4,2,'2026-01-04','JM Road'),
(5,8,'2026-01-05','Accounts Office'),
(6,9,'2026-01-06','Library'),
(7,3,'2026-01-07','Shivaji Nagar'),
(8,10,'2026-01-08','CSE Dept'),
(9,11,'2026-01-09','Parking Area'),
(10,12,'2026-01-10','Hostel');

-- -----------------------------------------
-- 7. AUTHORITY

CREATE TABLE Authority (
    AuthorityID INT PRIMARY KEY AUTO_INCREMENT,
    Aname VARCHAR(100),
    Department VARCHAR(100)
);

INSERT INTO Authority (Aname, Department) VALUES
-- Traffic authorities
('Pune Traffic Police','Traffic'),
('Mumbai Traffic Police','Traffic'),
('RTO Pune','Transport'),
('RTO Mumbai','Transport'),
('Highway Police','Highway'),
('City Police','Traffic'),
('E-Challan Dept','Digital'),
('Transport Dept','Transport'),
('Smart City Traffic','Traffic'),
('State Traffic Authority','Traffic'),
-- College authorities
('SIT Pune CSE Department','Academic'),
('SIT Pune Mechanical Department','Academic'),
('SIT Pune Library','Library'),
('SIT Pune Hostel Office','Hostel'),
('SIT Pune Accounts Office','Finance'),
('SIT Pune Discipline Committee','Administration'),
('SIT Pune Examination Cell','Academic'),
('SIT Pune Training & Placement Cell','Academic');

-- -----------------------------------------
-- 8. FINE

CREATE TABLE Fine (
    FineID INT PRIMARY KEY AUTO_INCREMENT,
    ViolationID INT NOT NULL,
    AuthorityID INT NOT NULL,
    IssueDate DATE,
    DueDate DATE,
    FineAmount DECIMAL(10,2) CHECK (FineAmount >= 0),
    Status ENUM('Paid','Unpaid') DEFAULT 'Unpaid',
    FOREIGN KEY (ViolationID) REFERENCES Violation(ViolationID),
    FOREIGN KEY (AuthorityID) REFERENCES Authority(AuthorityID)
);

INSERT INTO Fine (ViolationID, AuthorityID, IssueDate, DueDate, FineAmount, Status) VALUES
-- Traffic fines
(1,1,'2026-01-01','2026-01-10',500,'Unpaid'),
(4,2,'2026-01-04','2026-01-14',1000,'Paid'),
(7,1,'2026-01-07','2026-01-17',700,'Unpaid'),
-- College fines
(2,11,'2026-01-02','2026-01-12',600,'Paid'),
(3,11,'2026-01-03','2026-01-13',800,'Unpaid'),
(5,15,'2026-01-05','2026-01-15',1200,'Unpaid'),
(6,13,'2026-01-06','2026-01-16',1500,'Unpaid'),
(8,11,'2026-01-08','2026-01-18',200,'Paid'),
(9,16,'2026-01-09','2026-01-19',150,'Unpaid'),
(10,14,'2026-01-10','2026-01-20',250,'Unpaid');

-- Update TotalFine for accounts that have fines
UPDATE Account SET TotalFine = 500 WHERE AccountID = 1;
UPDATE Account SET TotalFine = 600 WHERE AccountID = 2;
UPDATE Account SET TotalFine = 800 WHERE AccountID = 3;
UPDATE Account SET TotalFine = 1000 WHERE AccountID = 4;
UPDATE Account SET TotalFine = 1200 WHERE AccountID = 5;
UPDATE Account SET TotalFine = 1500 WHERE AccountID = 6;
UPDATE Account SET TotalFine = 700 WHERE AccountID = 7;
UPDATE Account SET TotalFine = 200 WHERE AccountID = 8;
UPDATE Account SET TotalFine = 150 WHERE AccountID = 9;
UPDATE Account SET TotalFine = 250 WHERE AccountID = 10;

-- -----------------------------------------
-- 9. PENALTY

CREATE TABLE Penalty (
    FineID INT PRIMARY KEY,
    PenaltyPerDay DECIMAL(10,2) CHECK (PenaltyPerDay >= 0),
    FOREIGN KEY (FineID) REFERENCES Fine(FineID)
);

INSERT INTO Penalty (FineID, PenaltyPerDay) VALUES
(1,50),(3,70),(5,30),(6,40),(9,100),(10,120);

-- -----------------------------------------
-- 10. PAYMENT

CREATE TABLE Payment (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    FineID INT,
    PaymentDate DATE,
    PaymentMode ENUM('UPI','Card','NetBanking','Cash'),
    PaymentStatus ENUM('Success','Failed','Pending') DEFAULT 'Pending',
    AmountPaid DECIMAL(10,2) CHECK (AmountPaid >= 0),
    FOREIGN KEY (FineID) REFERENCES Fine(FineID)
);

INSERT INTO Payment (FineID, PaymentDate, PaymentMode, PaymentStatus, AmountPaid) VALUES
(2,'2026-01-05','UPI','Success',1000),
(4,'2026-01-06','Card','Success',600),
(8,'2026-01-09','UPI','Success',200);

-- -----------------------------------------
-- 11. AUDIT LOG

CREATE TABLE AuditLog (
    LogID INT PRIMARY KEY AUTO_INCREMENT,
    Action VARCHAR(100),
    TableName VARCHAR(50),
    ActionTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Pre-populate audit log with existing payment records
INSERT INTO AuditLog (Action, TableName) VALUES
('Payment Inserted','Payment'),
('Payment Inserted','Payment'),
('Payment Inserted','Payment');

-- -----------------------------------------
-- 12. FINE HISTORY

CREATE TABLE FineHistory (
    HistoryID INT PRIMARY KEY AUTO_INCREMENT,
    FineID INT,
    OldAmount DECIMAL(10,2),
    NewAmount DECIMAL(10,2),
    ChangeDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (FineID) REFERENCES Fine(FineID)
);

-- =========================================
-- TRIGGERS
-- =========================================
DELIMITER $$

-- Trigger 1: Auto-update Fine status to 'Paid' on successful payment
CREATE TRIGGER trg_update_fine_status
AFTER INSERT ON Payment
FOR EACH ROW
BEGIN
    IF NEW.PaymentStatus = 'Success' THEN
        UPDATE Fine SET Status = 'Paid'
        WHERE FineID = NEW.FineID;
    END IF;
END$$

-- Trigger 2: Auto-update Account.TotalFine when a new Fine is inserted
CREATE TRIGGER trg_update_total_fine
AFTER INSERT ON Fine
FOR EACH ROW
BEGIN
    UPDATE Account a
    JOIN Violation v ON a.AccountID = v.AccountID
    SET a.TotalFine = a.TotalFine + NEW.FineAmount
    WHERE v.ViolationID = NEW.ViolationID;
END$$

-- Trigger 3: Track fine amount changes in FineHistory
CREATE TRIGGER trg_fine_history
BEFORE UPDATE ON Fine
FOR EACH ROW
BEGIN
    IF OLD.FineAmount <> NEW.FineAmount THEN
        INSERT INTO FineHistory(FineID, OldAmount, NewAmount)
        VALUES (OLD.FineID, OLD.FineAmount, NEW.FineAmount);
    END IF;
END$$

-- Trigger 4: Audit log for every payment insertion
CREATE TRIGGER trg_audit_payment
AFTER INSERT ON Payment
FOR EACH ROW
BEGIN
    INSERT INTO AuditLog(Action, TableName)
    VALUES ('Payment Inserted', 'Payment');
END$$

-- Trigger 5: Audit log for every new fine issued
CREATE TRIGGER trg_audit_fine
AFTER INSERT ON Fine
FOR EACH ROW
BEGIN
    INSERT INTO AuditLog(Action, TableName)
    VALUES ('Fine Issued', 'Fine');
END$$

-- Trigger 6: Audit log for user signup (new account created)
CREATE TRIGGER trg_audit_signup
AFTER INSERT ON Account
FOR EACH ROW
BEGIN
    INSERT INTO AuditLog(Action, TableName)
    VALUES ('New Account Created', 'Account');
END$$

DELIMITER ;

-- =========================================
-- INDEXES
-- =========================================
CREATE INDEX idx_email ON Users(Email);
CREATE INDEX idx_fine_status ON Fine(Status);
CREATE INDEX idx_due_status ON Fine(DueDate, Status);

-- =========================================
-- VIEWS
-- =========================================

-- View 1: Show all unpaid fines with user details
CREATE VIEW PendingFines AS
SELECT u.FirstName, u.LastName, f.FineID, 
       (f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS FineAmount, 
       a.AccountID
FROM Users u
JOIN Account a ON u.UserID = a.UserID
JOIN Violation v ON a.AccountID = v.AccountID
JOIN Fine f ON v.ViolationID = f.ViolationID
LEFT JOIN Penalty p ON f.FineID = p.FineID
WHERE f.Status = 'Unpaid';

-- View 2: Total fine aggregated per user (includes dynamic penalty)
CREATE VIEW TotalFinePerUser AS
SELECT u.UserID, u.FirstName, u.LastName, 
       SUM(f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS Total
FROM Users u
JOIN Account a ON u.UserID = a.UserID
JOIN Violation v ON a.AccountID = v.AccountID
JOIN Fine f ON v.ViolationID = f.ViolationID
LEFT JOIN Penalty p ON f.FineID = p.FineID
GROUP BY u.UserID, u.FirstName, u.LastName;

-- View 3: Overdue fines with days overdue
CREATE VIEW OverdueFines AS
SELECT FineID, DATEDIFF(CURDATE(), DueDate) AS OverdueDays
FROM Fine
WHERE Status = 'Unpaid';

-- =========================================
-- STORED PROCEDURE
-- =========================================
DELIMITER $$

-- Procedure 1: Calculate total fine for a given account (includes dynamic penalty)
CREATE PROCEDURE GetUserTotalFine(IN acc_id INT)
BEGIN
    SELECT SUM(f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS TotalFine
    FROM Fine f
    JOIN Violation v ON f.ViolationID = v.ViolationID
    LEFT JOIN Penalty p ON f.FineID = p.FineID
    WHERE v.AccountID = acc_id;
END$$

-- Procedure 2: Get all fines filtered by status ('Paid' or 'Unpaid')
CREATE PROCEDURE GetFinesByStatus(IN fine_status VARCHAR(10))
BEGIN
    SELECT f.FineID, CONCAT(u.FirstName, ' ', u.LastName) AS UserName,
           vt.VName, v.Location, f.IssueDate, f.DueDate,
           f.FineAmount AS BaseFine,
           IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0) AS Penalty,
           (f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS TotalAmount,
           f.Status
    FROM Fine f
    JOIN Violation v ON f.ViolationID = v.ViolationID
    JOIN ViolationType vt ON v.ViolationTypeID = vt.ViolationTypeID
    JOIN Account a ON v.AccountID = a.AccountID
    JOIN Users u ON a.UserID = u.UserID
    LEFT JOIN Penalty p ON f.FineID = p.FineID
    WHERE f.Status = fine_status
    ORDER BY f.FineID DESC;
END$$

DELIMITER ;

-- =========================================
-- FUNCTIONS
-- =========================================
DELIMITER $$

-- Function 1: Get current total outstanding fine balance for a user by Email
CREATE FUNCTION getCurrentFineBalance(in_email VARCHAR(100))
RETURNS DECIMAL(10,2)
READS SQL DATA
BEGIN
    DECLARE cur_bal DECIMAL(10,2);
    SELECT SUM(
        f.FineAmount + IFNULL(
            IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate,
               DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay,
               0),
            0)
    ) INTO cur_bal
    FROM Users u
    JOIN Account a   ON u.UserID      = a.UserID
    JOIN Violation v ON a.AccountID   = v.AccountID
    JOIN Fine f      ON v.ViolationID = f.ViolationID
    LEFT JOIN Penalty p ON f.FineID   = p.FineID
    WHERE u.Email = in_email
      AND f.Status = 'Unpaid';

    RETURN IFNULL(cur_bal, 0.00);
END$$
-- SELECT getCurrentFineBalance('rahul@gmail.com') AS Balance;


-- Function 2: Count total number of violations for a given account
CREATE FUNCTION countUserViolations(acc_id INT)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count
    FROM Violation
    WHERE AccountID = acc_id;
    RETURN IFNULL(v_count, 0);
END$$

DELIMITER ;
-- SELECT countUserViolations(1) AS Count;