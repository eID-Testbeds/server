DROP TABLE IF EXISTS Test_Case_Steps CASCADE
;
DROP TABLE IF EXISTS Test_Cases CASCADE
;
DROP TABLE IF EXISTS Test_Messages CASCADE
;

CREATE TABLE Test_Case_Steps ( 
	ID bigint NOT NULL,
	Test_Case_Name varchar(500) NOT NULL,
	Step bigint NOT NULL,
	Message_Name varchar(50) NOT NULL,
	Inbound boolean
)
;

CREATE TABLE Test_Cases ( 
	Name varchar(500) NOT NULL
)
;

CREATE TABLE Test_Messages ( 
	Message_Name varchar(50) NOT NULL,
	Message text NOT NULL
)
;


ALTER TABLE Test_Case_Steps
	ADD CONSTRAINT UQ_Test_Case_Step UNIQUE (Step)
;
ALTER TABLE Test_Case_Steps
	ADD CONSTRAINT UQ_Test_Case_Name UNIQUE (Test_Case_Name)
;
CREATE INDEX IXFK_Test_Case_Steps_Test_Cases
	ON Test_Case_Steps (Test_Case_Name)
;
CREATE INDEX IXFK_Test_Case_Test_Messages
	ON Test_Case_Steps (Message_Name)
;
ALTER TABLE Test_Case_Steps
	ADD CONSTRAINT UQ_Test_Case_Steps_Message_Name UNIQUE (Message_Name)
;
ALTER TABLE Test_Messages
	ADD CONSTRAINT UQ_Test_Messages_Message_Name UNIQUE (Message_Name)
;
ALTER TABLE Test_Case_Steps ADD CONSTRAINT PK_Test_Case 
	PRIMARY KEY (ID)
;


ALTER TABLE Test_Cases ADD CONSTRAINT PK_Test_ 
	PRIMARY KEY (Name)
;


ALTER TABLE Test_Messages ADD CONSTRAINT PK_Test_Messages 
	PRIMARY KEY (Message_Name)
;




ALTER TABLE Test_Case_Steps ADD CONSTRAINT FK_Test_Case_Steps_Test_Cases 
	FOREIGN KEY (Test_Case_Name) REFERENCES Test_Cases (Name)
ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE Test_Case_Steps ADD CONSTRAINT FK_Test_Case_Test_Messages 
	FOREIGN KEY (Message_Name) REFERENCES Test_Messages (Message_Name)
;
