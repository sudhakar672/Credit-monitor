-- ============================================================
--  Bank Credit Risk Monitor — Schema + Seed Data
--  Database: credit_monitor_db
-- ============================================================

-- Borrowers master table
CREATE TABLE IF NOT EXISTS borrowers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrower_code   VARCHAR(20)  NOT NULL UNIQUE,
    name            VARCHAR(120) NOT NULL,
    segment         VARCHAR(50)  NOT NULL,   -- Corporate, MSME, Housing Loan, Trade Finance, Startup, Personal Loan
    city            VARCHAR(60),
    state           VARCHAR(60),
    contact_email   VARCHAR(100),
    pan_number      VARCHAR(10)  UNIQUE,
    gstin           VARCHAR(15),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Credit accounts (one borrower can have multiple accounts)
CREATE TABLE IF NOT EXISTS credit_accounts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number  VARCHAR(20)  NOT NULL UNIQUE,
    borrower_id     BIGINT       NOT NULL,
    account_type    VARCHAR(40)  NOT NULL,   -- Term Loan, CC, OD, LC, BG
    sanctioned_limit DECIMAL(15,2) NOT NULL,
    outstanding     DECIMAL(15,2) NOT NULL,
    utilization_pct DECIMAL(5,2) GENERATED ALWAYS AS (ROUND((outstanding / sanctioned_limit) * 100, 2)) STORED,
    interest_rate   DECIMAL(5,2),
    disbursement_date DATE,
    maturity_date   DATE,
    status          VARCHAR(20)  DEFAULT 'ACTIVE', -- ACTIVE, NPA, CLOSED, WATCH
    dpd             INT          DEFAULT 0,         -- Days Past Due
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (borrower_id) REFERENCES borrowers(id)
);

-- CIBIL score history
CREATE TABLE IF NOT EXISTS cibil_scores (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrower_id     BIGINT       NOT NULL,
    score           INT          NOT NULL,
    score_date      DATE         NOT NULL,
    risk_band       VARCHAR(20),  -- Prime (750+), Near-prime (650-749), Subprime (550-649), High Risk (<550)
    remarks         VARCHAR(200),
    FOREIGN KEY (borrower_id) REFERENCES borrowers(id)
);

-- Alerts / flags
CREATE TABLE IF NOT EXISTS credit_alerts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrower_id     BIGINT       NOT NULL,
    alert_type      VARCHAR(30)  NOT NULL,  -- CIBIL_DROP, HIGH_UTILIZATION, DELINQUENCY, COVENANT_BREACH, INFO
    severity        VARCHAR(10)  NOT NULL,  -- CRITICAL, WARNING, INFO, OK
    message         TEXT,
    is_resolved     BOOLEAN      DEFAULT FALSE,
    alert_time      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    resolved_at     DATETIME,
    FOREIGN KEY (borrower_id) REFERENCES borrowers(id)
);

-- Monthly NPA trend
CREATE TABLE IF NOT EXISTS npa_trend (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    month_year      VARCHAR(10)  NOT NULL UNIQUE,  -- e.g. Apr-25
    npa_ratio       DECIMAL(5,2) NOT NULL,
    total_advances  DECIMAL(15,2),
    npa_amount      DECIMAL(15,2),
    recorded_at     DATE
);

-- Portfolio summary (monthly snapshot)
CREATE TABLE IF NOT EXISTS portfolio_summary (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    snapshot_date   DATE         NOT NULL UNIQUE,
    total_exposure  DECIMAL(15,2),
    avg_cibil       INT,
    npa_ratio       DECIMAL(5,2),
    watchlist_count INT,
    pcr             DECIMAL(5,2),  -- Provision Coverage Ratio
    crar            DECIMAL(5,2),  -- Capital Adequacy Ratio
    ldr             DECIMAL(5,2)   -- Loan to Deposit Ratio
);

-- ============================================================
--  SEED DATA
-- ============================================================

INSERT IGNORE INTO borrowers (borrower_code, name, segment, city, state, contact_email, pan_number, gstin) VALUES
('BRW001', 'Meridian Holdings Pvt. Ltd.',   'Corporate',     'Mumbai',    'Maharashtra', 'finance@meridianholdings.in',  'AABCM1234D', '27AABCM1234D1Z5'),
('BRW002', 'Apex Retail Group',             'MSME',          'Delhi',     'Delhi',       'accounts@apexretail.in',       'AADCA5678E', '07AADCA5678E1Z3'),
('BRW003', 'TechVenture Pvt. Ltd.',         'Startup',       'Bangalore', 'Karnataka',   'cfo@techventure.io',           'AABCT9012F', '29AABCT9012F1Z1'),
('BRW004', 'Global Imports Co.',            'Trade Finance', 'Chennai',   'Tamil Nadu',  'trade@globalimports.in',       'AABCG3456G', '33AABCG3456G1Z7'),
('BRW005', 'Sunrise Agro Foods Ltd.',       'MSME',          'Pune',      'Maharashtra', 'finance@sunriseagro.in',       'AABCS7890H', '27AABCS7890H1Z2'),
('BRW006', 'Metro Infra Ltd.',              'Corporate',     'Hyderabad', 'Telangana',   'accounts@metroinfra.in',       'AABCM2345I', '36AABCM2345I1Z8'),
('BRW007', 'Sunrise Textiles',             'MSME',          'Surat',     'Gujarat',     'finance@surisetextiles.in',    'AABCS6789J', '24AABCS6789J1Z4'),
('BRW008', 'Sunset Properties Ltd.',        'Housing Loan',  'Mumbai',    'Maharashtra', 'legal@sunsetprop.in',          'AABCS1234K', '27AABCS1234K1Z6'),
('BRW009', 'Bharat Auto Components',        'MSME',          'Chennai',   'Tamil Nadu',  'finance@bharatauto.in',        'AABCB4567L', '33AABCB4567L1Z9'),
('BRW010', 'Himalaya Pharma Pvt. Ltd.',     'Corporate',     'Ahmedabad', 'Gujarat',     'accounts@himalayapharma.in',   'AABCH8901M', '24AABCH8901M1Z3'),
('BRW011', 'Pioneer Logistics Ltd.',        'Trade Finance', 'Kolkata',   'West Bengal', 'finance@pioneerlogistics.in',  'AABCP2345N', '19AABCP2345N1Z5'),
('BRW012', 'Rajasthan Minerals Corp.',      'Corporate',     'Jaipur',    'Rajasthan',   'accounts@rajminerals.in',      'AABCR6789O', '08AABCR6789O1Z1'),
('BRW013', 'Coastal Fisheries Pvt. Ltd.',   'MSME',          'Kochi',     'Kerala',      'finance@coastalfisheries.in',  'AABCC1234P', '32AABCC1234P1Z7'),
('BRW014', 'NovaSoft Technologies',         'Startup',       'Bangalore', 'Karnataka',   'cfo@novasoft.io',              'AABCN5678Q', '29AABCN5678Q1Z2'),
('BRW015', 'Delhi Food Courts Pvt. Ltd.',   'MSME',          'Delhi',     'Delhi',       'finance@delhifoodcourts.in',   'AABCD9012R', '07AABCD9012R1Z8');

INSERT IGNORE INTO credit_accounts (account_number, borrower_id, account_type, sanctioned_limit, outstanding, interest_rate, disbursement_date, maturity_date, status, dpd) VALUES
('ACC2024001',  1,  'Term Loan',   75000000.00,  68250000.00, 11.50, '2022-04-01', '2027-03-31', 'NPA',    95),
('ACC2024002',  1,  'CC',          10000000.00,   9100000.00, 13.00, '2023-01-15', '2024-12-31', 'WATCH',  35),
('ACC2024003',  2,  'OD',          38000000.00,  28120000.00, 12.75, '2023-06-01', '2026-05-31', 'ACTIVE', 0),
('ACC2024004',  3,  'Term Loan',   18600000.00,  16368000.00, 14.00, '2023-09-01', '2026-08-31', 'WATCH',  15),
('ACC2024005',  4,  'LC',          70600000.00,  43086000.00, 10.50, '2022-11-01', '2027-10-31', 'WATCH',  30),
('ACC2024006',  5,  'Term Loan',   14000000.00,   9660000.00, 12.00, '2023-03-15', '2026-03-14', 'ACTIVE', 0),
('ACC2024007',  6,  'Term Loan',  120000000.00,  89200000.00, 10.25, '2021-07-01', '2028-06-30', 'ACTIVE', 0),
('ACC2024008',  7,  'CC',          10000000.00,   5200000.00, 13.50, '2023-08-01', '2025-07-31', 'ACTIVE', 0),
('ACC2024009',  8,  'Home Loan',  240000000.00, 106900000.00,  8.75, '2020-01-15', '2040-01-14', 'ACTIVE', 0),
('ACC2024010',  9,  'Term Loan',   25000000.00,  18250000.00, 12.50, '2023-05-01', '2026-04-30', 'ACTIVE', 0),
('ACC2024011', 10,  'Term Loan',   80000000.00,  56000000.00, 10.75, '2022-09-01', '2027-08-31', 'ACTIVE', 0),
('ACC2024012', 11,  'BG',          35000000.00,  21000000.00, 11.00, '2023-02-01', '2026-01-31', 'ACTIVE', 0),
('ACC2024013', 12,  'Term Loan',   60000000.00,  39000000.00, 11.25, '2022-06-01', '2027-05-31', 'ACTIVE', 0),
('ACC2024014', 13,  'OD',           8000000.00,   5600000.00, 13.75, '2023-10-01', '2025-09-30', 'ACTIVE', 0),
('ACC2024015', 14,  'Term Loan',   12000000.00,   7200000.00, 15.00, '2024-01-01', '2027-12-31', 'ACTIVE', 0),
('ACC2024016', 15,  'CC',           6000000.00,   4200000.00, 14.50, '2023-11-01', '2025-10-31', 'ACTIVE', 0);

INSERT IGNORE INTO cibil_scores (borrower_id, score, score_date, risk_band, remarks) VALUES
(1,  580, '2025-12-01', 'Subprime',   'Score declined due to delayed payments'),
(1,  498, '2026-03-01', 'High Risk',  'Covenant breach risk — further decline'),
(2,  615, '2025-12-01', 'Near-prime', 'Stable'),
(2,  601, '2026-03-01', 'Near-prime', 'Slight dip — utilization rising'),
(3,  650, '2025-12-01', 'Near-prime', 'Good'),
(3,  634, '2026-03-01', 'Near-prime', 'High utilization impacting score'),
(4,  680, '2025-12-01', 'Near-prime', 'Watch for delinquency'),
(4,  668, '2026-03-01', 'Near-prime', '30-day overdue on revolving'),
(5,  650, '2025-12-01', 'Near-prime', 'Seasonal dip'),
(5,  644, '2026-03-01', 'Near-prime', 'Agri cycle impact'),
(6,  715, '2025-12-01', 'Prime',      'Good standing'),
(6,  711, '2026-03-01', 'Prime',      'Marginal dip'),
(7,  730, '2025-12-01', 'Prime',      'Stable repayment'),
(7,  728, '2026-03-01', 'Prime',      'Consistent'),
(8,  745, '2025-12-01', 'Prime',      'Excellent'),
(8,  744, '2026-03-01', 'Prime',      'No issues'),
(9,  720, '2026-03-01', 'Prime',      'Regular EMI payer'),
(10, 760, '2026-03-01', 'Prime',      'Strong financials'),
(11, 700, '2026-03-01', 'Prime',      'Trade finance — good'),
(12, 735, '2026-03-01', 'Prime',      'Mining — stable'),
(13, 680, '2026-03-01', 'Near-prime', 'MSME — seasonal variation'),
(14, 690, '2026-03-01', 'Near-prime', 'Early stage startup'),
(15, 610, '2026-03-01', 'Near-prime', 'Food business — watch');

INSERT IGNORE INTO credit_alerts (borrower_id, alert_type, severity, message, is_resolved, alert_time) VALUES
(1, 'CIBIL_DROP',        'CRITICAL', 'CIBIL score dropped by 82 points — covenant breach risk identified. Immediate review required.', FALSE, '2026-03-16 09:14:00'),
(3, 'HIGH_UTILIZATION',  'WARNING',  'Credit utilization exceeds 88% threshold. Exposure limit may be breached.', FALSE, '2026-03-16 08:47:00'),
(4, 'DELINQUENCY',       'WARNING',  '30-day delinquency recorded on revolving credit line. Collections team notified.', FALSE, '2026-03-16 07:32:00'),
(6, 'INFO',              'INFO',     'Q1 FY26 stress test results now available. Portfolio review scheduled for 31-Mar-2026.', FALSE, '2026-03-16 06:00:00'),
(8, 'INFO',              'OK',       'EMI received on time for March 2026. No action required.', TRUE,  '2026-03-16 05:10:00'),
(2, 'HIGH_UTILIZATION',  'WARNING',  'OD utilization at 74% — approaching threshold. Monitor closely.', FALSE, '2026-03-15 14:20:00'),
(5, 'INFO',              'INFO',     'Seasonal dip in score expected. Agriculture cycle — reassess in June 2026.', FALSE, '2026-03-15 10:00:00'),
(1, 'COVENANT_BREACH',   'CRITICAL', 'Debt service coverage ratio below 1.0 — covenant breach. Legal team informed.', FALSE, '2026-03-14 16:30:00');

INSERT IGNORE INTO npa_trend (month_year, npa_ratio, total_advances, npa_amount, recorded_at) VALUES
('Apr-25', 1.92, 3000000000.00,  57600000.00, '2025-04-30'),
('May-25', 1.98, 3050000000.00,  60390000.00, '2025-05-31'),
('Jun-25', 2.05, 3100000000.00,  63550000.00, '2025-06-30'),
('Jul-25', 2.01, 3150000000.00,  63315000.00, '2025-07-31'),
('Aug-25', 2.10, 3200000000.00,  67200000.00, '2025-08-31'),
('Sep-25', 2.14, 3250000000.00,  69550000.00, '2025-09-30'),
('Oct-25', 2.18, 3300000000.00,  71940000.00, '2025-10-31'),
('Nov-25', 2.22, 3350000000.00,  74370000.00, '2025-11-30'),
('Dec-25', 2.20, 3380000000.00,  74360000.00, '2025-12-31'),
('Jan-26', 2.25, 3400000000.00,  76500000.00, '2026-01-31'),
('Feb-26', 2.26, 3430000000.00,  77518000.00, '2026-02-28'),
('Mar-26', 2.38, 3520000000.00,  83776000.00, '2026-03-31');

INSERT IGNORE INTO portfolio_summary (snapshot_date, total_exposure, avg_cibil, npa_ratio, watchlist_count, pcr, crar, ldr) VALUES
('2026-03-16', 3520000000.00, 712, 2.38, 47, 68.4, 15.2, 74.8);
