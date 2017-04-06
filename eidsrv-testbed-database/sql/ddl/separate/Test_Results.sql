DROP TABLE IF EXISTS Log_Messages CASCADE
;
DROP TABLE IF EXISTS Test_Results CASCADE
;

CREATE TABLE Log_Messages ( 
	ID bigint NOT NULL,
	Step_Number bigint NOT NULL,
	Message text NOT NULL,
	Success boolean NOT NULL
)
;

CREATE TABLE Test_Results ( 
	ID bigint NOT NULL,
	Test_Case_Name varchar(500) NOT NULL,
	Profile_ID bigint NOT NULL,
	Log_ID bigint NOT NULL,
	Step_ID bigint NOT NULL
)
;


CREATE INDEX IXFK_Test_Results_Test_Cases
	ON Test_Results (Test_Case_Name)
;
CREATE INDEX IXFK_Test_Results_Log_Messages
	ON Test_Results (Log_ID, Step_ID)
;
ALTER TABLE Test_Results
	ADD CONSTRAINT UQ_Test_Results_Profile_ID UNIQUE (Profile_ID)
;
ALTER TABLE Test_Results
	ADD CONSTRAINT UQ_Test_Results_Test_Case_Name UNIQUE (Test_Case_Name)
;
ALTER TABLE Test_Results
	ADD CONSTRAINT UQ_Test_Results_Log_ID UNIQUE (Log_ID)
;
ALTER TABLE Test_Results
	ADD CONSTRAINT UQ_Test_Results_Step_ID UNIQUE (Step_ID)
;
ALTER TABLE Log_Messages ADD CONSTRAINT PK_Log_Messages 
	PRIMARY KEY (ID, Step_Number)
;


ALTER TABLE Test_Results ADD CONSTRAINT PK_Test_Results 
	PRIMARY KEY (ID)
;




ALTER TABLE Test_Results ADD CONSTRAINT FK_Test_Results_Log_Messages 
	FOREIGN KEY (Log_ID, Step_ID) REFERENCES Log_Messages (ID, Step_Number)
ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE Test_Results ADD CONSTRAINT FK_Test_Results_Test_Cases 
	FOREIGN KEY (Test_Case_Name) REFERENCES Test_Cases (Name)
;

ALTER TABLE Test_Results ADD CONSTRAINT FK_Test_Results_Test_Profile 
	FOREIGN KEY (Profile_ID) REFERENCES Test_Profile (ID)
;
