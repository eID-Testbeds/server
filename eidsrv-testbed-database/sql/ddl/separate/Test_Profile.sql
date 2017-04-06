DROP TABLE IF EXISTS Bit_Length CASCADE
;
DROP TABLE IF EXISTS Chip_Authentication_Algorithm CASCADE
;
DROP TABLE IF EXISTS Elliptic_Curves CASCADE
;
DROP TABLE IF EXISTS Mandatory_Profile CASCADE
;
DROP TABLE IF EXISTS Optional_Profile CASCADE
;
DROP TABLE IF EXISTS P_CAA_Link CASCADE
;
DROP TABLE IF EXISTS P_MP_Link CASCADE
;
DROP TABLE IF EXISTS P_OP_Link CASCADE
;
DROP TABLE IF EXISTS P_TLS_Link CASCADE
;
DROP TABLE IF EXISTS P_XE_Link CASCADE
;
DROP TABLE IF EXISTS P_XS_Link CASCADE
;
DROP TABLE IF EXISTS Test_Case_Set CASCADE
;
DROP TABLE IF EXISTS Test_Profile CASCADE
;
DROP TABLE IF EXISTS TLS CASCADE
;
DROP TABLE IF EXISTS XML_Encryption CASCADE
;
DROP TABLE IF EXISTS XML_Signature CASCADE
;

CREATE TABLE Bit_Length ( 
	Bit_Length integer DEFAULT 2048 NOT NULL
)
;

CREATE TABLE Chip_Authentication_Algorithm ( 
	ID bigint NOT NULL,
	Algorithm varchar(100) NOT NULL
)
;

CREATE TABLE Elliptic_Curves ( 
	ID bigint NOT NULL,
	Curve varchar(100)
)
;

CREATE TABLE Mandatory_Profile ( 
	ID bigint NOT NULL,
	Mandatory_Profile varchar(50) NOT NULL
)
;

CREATE TABLE Optional_Profile ( 
	ID bigint NOT NULL,
	OPTIONAL_PROFILE varchar(50) NOT NULL
)
;

CREATE TABLE P_CAA_Link ( 
	ID bigint NOT NULL,
	P_ID bigint NOT NULL,
	CAA_ID bigint NOT NULL
)
;

CREATE TABLE P_MP_Link ( 
	ID bigint NOT NULL,
	MP_ID bigint NOT NULL,
	P_ID bigint NOT NULL
)
;

CREATE TABLE P_OP_Link ( 
	ID bigint NOT NULL,
	P_ID bigint NOT NULL,
	OP_ID bigint NOT NULL
)
;

CREATE TABLE P_TLS_Link ( 
	ID bigint NOT NULL,
	P_ID bigint NOT NULL,
	TLS_ID bigint NOT NULL
)
;

CREATE TABLE P_XE_Link ( 
	ID bigint NOT NULL,
	P_ID bigint NOT NULL,
	XE_ID bigint NOT NULL
)
;

CREATE TABLE P_XS_Link ( 
	ID bigint NOT NULL,
	P_ID bigint NOT NULL,
	XS_ID bigint NOT NULL
)
;

CREATE TABLE Test_Case_Set ( 
	ID bigint NOT NULL,
	Set_ID bigint NOT NULL,
	Test_Case_Name varchar(500) NOT NULL
)
;

CREATE TABLE Test_Profile ( 
	ID bigint NOT NULL,
	Candidate_URL varchar(2083) NOT NULL,
	Vendor varchar(50) NOT NULL,
	Name varchar(50) NOT NULL,
	Version_Major integer NOT NULL,
	Version_Minor integer,
	Version_Subminor integer,
	API_Major integer NOT NULL,
	API_Minor integer,
	API_Subminor integer,
	Connection varchar(50) DEFAULT 'TLS2',
	TLS_ID bigint,
	Mandatory_Profile bigint,
	Optional_Profile bigint,
	XML_Signature bigint,
	XML_Encryption_Algorithm bigint,
	Chip_Authentication_ID bigint,
	Test_Case_Set_ID bigint NOT NULL
)
;

CREATE TABLE TLS ( 
	ID bigint NOT NULL,
	TLS_Version varchar(50) DEFAULT 1.2 NOT NULL,
	TLS_Ciphersuite varchar(500) NOT NULL,
	TLS_Curve bigint,
	TLS_Asymmetric_Cipher varchar(50) DEFAULT 'RSA',
	TLS_Bit_Length integer
)
;

CREATE TABLE XML_Encryption ( 
	ID bigint NOT NULL,
	Algorithm varchar(100) NOT NULL
)
;

CREATE TABLE XML_Signature ( 
	ID bigint NOT NULL,
	Signature_Method varchar(2083) NOT NULL,
	Digest_Method varchar(100) NOT NULL,
	Canonicalization_Method varchar(100) NOT NULL,
	Bit_Length integer NOT NULL,
	ECDSA_Curve bigint
)
;


CREATE INDEX IXFK_EC_Curves_Test_Profile
	ON Elliptic_Curves (ID)
;
ALTER TABLE P_CAA_Link
	ADD CONSTRAINT UQ_P_CAA_Link_P_ID UNIQUE (P_ID)
;
ALTER TABLE P_CAA_Link
	ADD CONSTRAINT UQ_P_CAA_Link_CAA_ID UNIQUE (CAA_ID)
;
CREATE INDEX IX_Profile_Chip_Authentication_Algorithm_1
	ON P_CAA_Link (P_ID, CAA_ID)
;
CREATE INDEX IX_Profile_Chip_Authentication_Algorithm_2
	ON P_CAA_Link (CAA_ID, P_ID)
;
ALTER TABLE P_MP_Link
	ADD CONSTRAINT UQ_P_MP_Link_MP_ID UNIQUE (MP_ID)
;
ALTER TABLE P_MP_Link
	ADD CONSTRAINT UQ_P_MP_Link_P_ID UNIQUE (P_ID)
;
CREATE INDEX IX_Profile_Mandatory_Profile_1
	ON P_MP_Link (P_ID, MP_ID)
;
CREATE INDEX IX_Profile_Mandatory_Profile_2
	ON P_MP_Link (MP_ID, P_ID)
;
ALTER TABLE P_OP_Link
	ADD CONSTRAINT UQ_P_OP_Link_P_ID UNIQUE (P_ID)
;
ALTER TABLE P_OP_Link
	ADD CONSTRAINT UQ_P_OP_Link_OP_ID UNIQUE (OP_ID)
;
CREATE INDEX IX_Profile_Optional_Profile_1
	ON P_OP_Link (OP_ID, P_ID)
;
CREATE INDEX IX_Profile_Optional_Profile_2
	ON P_OP_Link (P_ID, OP_ID)
;
ALTER TABLE P_TLS_Link
	ADD CONSTRAINT UQ_P_TLS_Link_P_ID UNIQUE (P_ID)
;
ALTER TABLE P_TLS_Link
	ADD CONSTRAINT UQ_P_TLS_Link_TLS_ID UNIQUE (TLS_ID)
;
CREATE INDEX IX_Profile_TLS_1
	ON P_TLS_Link (P_ID, TLS_ID)
;
CREATE INDEX IX_Profile_TLS_2
	ON P_TLS_Link (TLS_ID, P_ID)
;
ALTER TABLE P_XE_Link
	ADD CONSTRAINT UQ_P_XE_Link_P_ID UNIQUE (P_ID)
;
ALTER TABLE P_XE_Link
	ADD CONSTRAINT UQ_P_XE_Link_XE_ID UNIQUE (XE_ID)
;
CREATE INDEX IX_Profile_XML_Encryption_1
	ON P_XE_Link (P_ID, XE_ID)
;
CREATE INDEX IX_Profile_XML_Encryption_2
	ON P_XE_Link (XE_ID, P_ID)
;
ALTER TABLE P_XS_Link
	ADD CONSTRAINT UQ_P_XS_Link_P_ID UNIQUE (P_ID)
;
ALTER TABLE P_XS_Link
	ADD CONSTRAINT UQ_P_XS_Link_XS_ID UNIQUE (XS_ID)
;
CREATE INDEX IX_Profile_XML_Signature_1
	ON P_XS_Link (P_ID, XS_ID)
;
CREATE INDEX IX_Profile_XML_Signature_2
	ON P_XS_Link (XS_ID, P_ID)
;
CREATE INDEX IX_Test_Case_Set_Test_Cases_1
	ON Test_Case_Set (Set_ID, Test_Case_Name)
;
ALTER TABLE Test_Case_Set
	ADD CONSTRAINT UQ_Test_Case_Set_Test_Case_Name UNIQUE (Test_Case_Name)
;
ALTER TABLE Test_Case_Set
	ADD CONSTRAINT UQ_Test_Case_Set_Set_ID UNIQUE (Set_ID)
;
CREATE INDEX IX_Test_Case_Set_Test_Cases_2
	ON Test_Case_Set (Test_Case_Name, Set_ID)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_Chip_Authentication_ID UNIQUE (Chip_Authentication_ID)
;
CREATE INDEX IXFK_Test_Profile_Test_Case_Set
	ON Test_Profile (Test_Case_Set_ID)
;
CREATE INDEX IXFK_Test_Profile_Mandatory_Profile
	ON Test_Profile (Mandatory_Profile)
;
CREATE INDEX IXFK_Test_Profile_XML_Signature
	ON Test_Profile (XML_Signature)
;
CREATE INDEX IXFK_Test_Profile_TLS
	ON Test_Profile (TLS_ID)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_Candidate_URL UNIQUE (Candidate_URL)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_Connection UNIQUE (Connection)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_Test_Case_Set_ID UNIQUE (Test_Case_Set_ID)
;
CREATE INDEX IXFK_Test_Profile_Optional_Profile
	ON Test_Profile (Optional_Profile)
;
CREATE INDEX IXFK_Test_Profile_Chip_Authentication_Algorithm
	ON Test_Profile (Chip_Authentication_ID)
;
CREATE INDEX IXFK_Test_Profile_XML_Encryption
	ON Test_Profile (XML_Encryption_Algorithm)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_XML_Encryption_Algorithm UNIQUE (XML_Encryption_Algorithm)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_TLS UNIQUE (TLS_ID)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_Optional_Profile UNIQUE (Optional_Profile)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_Mandatory_Profile UNIQUE (Mandatory_Profile)
;
ALTER TABLE Test_Profile
	ADD CONSTRAINT UQ_Test_Profile_XML_Signature UNIQUE (XML_Signature)
;
CREATE INDEX IXFK_TLS_Bit_Length
	ON TLS (TLS_Bit_Length)
;
CREATE INDEX IXFK_TLS_Elliptic_Curves
	ON TLS (TLS_Curve)
;
ALTER TABLE XML_Encryption
	ADD CONSTRAINT UQ_XML_Encryption_Algorithm UNIQUE (Algorithm)
;
ALTER TABLE XML_Signature
	ADD CONSTRAINT UQ_XML_Signature_Bit_Length UNIQUE (Bit_Length)
;
ALTER TABLE XML_Signature
	ADD CONSTRAINT UQ_XML_Signature_Signature_Method UNIQUE (Signature_Method)
;
ALTER TABLE XML_Signature
	ADD CONSTRAINT UQ_XML_Signature_Digest_Method UNIQUE (Digest_Method)
;
ALTER TABLE XML_Signature
	ADD CONSTRAINT UQ_XML_Signature_Canonicalization_Method UNIQUE (Canonicalization_Method)
;
ALTER TABLE XML_Signature
	ADD CONSTRAINT UQ_XML_Signature_ECDSA_Curve UNIQUE (ECDSA_Curve)
;
CREATE INDEX IXFK_XML_Signature_Bit_Length
	ON XML_Signature (Bit_Length)
;
CREATE INDEX IXFK_XML_Signature_Elliptic_Curves
	ON XML_Signature (ECDSA_Curve)
;
ALTER TABLE Bit_Length ADD CONSTRAINT PK_Bit_Length 
	PRIMARY KEY (Bit_Length)
;


ALTER TABLE Chip_Authentication_Algorithm ADD CONSTRAINT PK_Chip_Authentication_Algorithm 
	PRIMARY KEY (ID)
;


ALTER TABLE Elliptic_Curves ADD CONSTRAINT PK_EC_Curves 
	PRIMARY KEY (ID)
;


ALTER TABLE Mandatory_Profile ADD CONSTRAINT PK_Mandatory_Profile 
	PRIMARY KEY (ID)
;


ALTER TABLE Optional_Profile ADD CONSTRAINT PK_Optional_Profile 
	PRIMARY KEY (ID)
;


ALTER TABLE P_CAA_Link ADD CONSTRAINT PK_P_CAA_Link 
	PRIMARY KEY (ID)
;


ALTER TABLE P_MP_Link ADD CONSTRAINT PK_P_MP_Link 
	PRIMARY KEY (ID)
;


ALTER TABLE P_OP_Link ADD CONSTRAINT PK_P_OP_Link 
	PRIMARY KEY (ID)
;


ALTER TABLE P_TLS_Link ADD CONSTRAINT PK_P_TLS_Link 
	PRIMARY KEY (ID)
;


ALTER TABLE P_XE_Link ADD CONSTRAINT PK_P_XE_Link 
	PRIMARY KEY (ID)
;


ALTER TABLE P_XS_Link ADD CONSTRAINT PK_P_XS_Link 
	PRIMARY KEY (ID)
;


ALTER TABLE Test_Case_Set ADD CONSTRAINT PK_Test_Case_Set 
	PRIMARY KEY (ID)
;


ALTER TABLE Test_Profile ADD CONSTRAINT PK_Test_Profile 
	PRIMARY KEY (ID)
;


ALTER TABLE TLS ADD CONSTRAINT PK_TLS 
	PRIMARY KEY (ID)
;


ALTER TABLE XML_Encryption ADD CONSTRAINT PK_XML_Encryption 
	PRIMARY KEY (ID)
;


ALTER TABLE XML_Signature ADD CONSTRAINT PK_XML_Signature 
	PRIMARY KEY (ID)
;




ALTER TABLE P_CAA_Link ADD CONSTRAINT FK_P_CAA_Link_Chip_Authentication_Algorithm 
	FOREIGN KEY (CAA_ID) REFERENCES Chip_Authentication_Algorithm (ID)
;

ALTER TABLE P_CAA_Link ADD CONSTRAINT FK_P_CAA_Link_Test_Profile 
	FOREIGN KEY (P_ID) REFERENCES Test_Profile (Chip_Authentication_ID)
;

ALTER TABLE P_MP_Link ADD CONSTRAINT FK_P_MP_Link_Mandatory_Profile 
	FOREIGN KEY (MP_ID) REFERENCES Mandatory_Profile (ID)
;

ALTER TABLE P_MP_Link ADD CONSTRAINT FK_P_MP_Link_Test_Profile 
	FOREIGN KEY (P_ID) REFERENCES Test_Profile (Mandatory_Profile)
;

ALTER TABLE P_OP_Link ADD CONSTRAINT FK_P_OP_Link_Optional_Profile 
	FOREIGN KEY (OP_ID) REFERENCES Optional_Profile (ID)
;

ALTER TABLE P_OP_Link ADD CONSTRAINT FK_P_OP_Link_Test_Profile 
	FOREIGN KEY (P_ID) REFERENCES Test_Profile (Optional_Profile)
;

ALTER TABLE P_TLS_Link ADD CONSTRAINT FK_P_TLS_Link_Test_Profile 
	FOREIGN KEY (P_ID) REFERENCES Test_Profile (TLS_ID)
;

ALTER TABLE P_TLS_Link ADD CONSTRAINT FK_P_TLS_Link_TLS 
	FOREIGN KEY (TLS_ID) REFERENCES TLS (ID)
;

ALTER TABLE P_XE_Link ADD CONSTRAINT FK_P_XE_Link_Test_Profile 
	FOREIGN KEY (P_ID) REFERENCES Test_Profile (XML_Encryption_Algorithm)
;

ALTER TABLE P_XE_Link ADD CONSTRAINT FK_P_XE_Link_XML_Encryption 
	FOREIGN KEY (XE_ID) REFERENCES XML_Encryption (ID)
;

ALTER TABLE P_XS_Link ADD CONSTRAINT FK_P_XS_Link_Test_Profile 
	FOREIGN KEY (P_ID) REFERENCES Test_Profile (XML_Signature)
;

ALTER TABLE P_XS_Link ADD CONSTRAINT FK_P_XS_Link_XML_Signature 
	FOREIGN KEY (XS_ID) REFERENCES XML_Signature (ID)
;

ALTER TABLE Test_Case_Set ADD CONSTRAINT FK_Test_Case_Set_Test_Cases 
	FOREIGN KEY (Test_Case_Name) REFERENCES Test_Cases (Name)
;

ALTER TABLE Test_Case_Set ADD CONSTRAINT FK_Test_Case_Set_Test_Profile 
	FOREIGN KEY (Set_ID) REFERENCES Test_Profile (Test_Case_Set_ID)
;

ALTER TABLE XML_Signature ADD CONSTRAINT FK_XML_Signature_Bit_Length 
	FOREIGN KEY (Bit_Length) REFERENCES Bit_Length (Bit_Length)
;

ALTER TABLE XML_Signature ADD CONSTRAINT FK_XML_Signature_Elliptic_Curves 
	FOREIGN KEY (ECDSA_Curve) REFERENCES Elliptic_Curves (ID)
;
