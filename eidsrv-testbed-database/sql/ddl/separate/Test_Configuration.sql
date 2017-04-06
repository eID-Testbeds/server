DROP TABLE IF EXISTS Profiles CASCADE
;
DROP TABLE IF EXISTS Roles CASCADE
;
DROP TABLE IF EXISTS Tokens CASCADE
;
DROP TABLE IF EXISTS Users CASCADE
;

CREATE TABLE Profiles ( 
	ID bigint NOT NULL,
	P_ID bigint NOT NULL,
	U_ID bigint NOT NULL
)
;

CREATE TABLE Roles ( 
	ID bigint NOT NULL,
	Role varchar(150) NOT NULL
)
;

CREATE TABLE Tokens ( 
	Token varchar(256) NOT NULL,
	User bigint NOT NULL
)
;

CREATE TABLE Users ( 
	ID bigint NOT NULL,
	Name varchar(500) NOT NULL,
	PWD_Hash varchar(256) NOT NULL,
	Role bigint NOT NULL
)
;


ALTER TABLE Profiles
	ADD CONSTRAINT UQ_Profiles_P_ID UNIQUE (P_ID)
;
ALTER TABLE Profiles
	ADD CONSTRAINT UQ_Profiles_U_ID UNIQUE (U_ID)
;
CREATE INDEX IX_Profile_Users_1
	ON Profiles (P_ID, U_ID)
;
CREATE INDEX IX_Profile_Users_2
	ON Profiles (U_ID, P_ID)
;
ALTER TABLE Roles
	ADD CONSTRAINT UQ_Roles_Role UNIQUE (Role)
;
ALTER TABLE Tokens
	ADD CONSTRAINT UQ_Tokens_User UNIQUE (User)
;
ALTER TABLE Users
	ADD CONSTRAINT UQ_Users_Name UNIQUE (Name)
;
ALTER TABLE Profiles ADD CONSTRAINT PK_Profiles 
	PRIMARY KEY (ID)
;


ALTER TABLE Roles ADD CONSTRAINT PK_Roles 
	PRIMARY KEY (ID)
;


ALTER TABLE Tokens ADD CONSTRAINT PK_Tokens 
	PRIMARY KEY (Token)
;


ALTER TABLE Users ADD CONSTRAINT PK_Users 
	PRIMARY KEY (ID)
;




ALTER TABLE Profiles ADD CONSTRAINT FK_Profiles_Test_Profile 
	FOREIGN KEY (P_ID) REFERENCES Test_Profile (ID)
;

ALTER TABLE Profiles ADD CONSTRAINT FK_Profiles_Users 
	FOREIGN KEY (U_ID) REFERENCES Users (ID)
;

ALTER TABLE Tokens ADD CONSTRAINT FK_Tokens_Users 
	FOREIGN KEY (User) REFERENCES Users (ID)
;

ALTER TABLE Users ADD CONSTRAINT FK_Users_Roles 
	FOREIGN KEY (Role) REFERENCES Roles (ID)
;
