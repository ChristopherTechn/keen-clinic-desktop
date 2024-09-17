CREATE TABLE "prescription" (
	"id"	INTEGER,
	"outpatientId"	INTEGER,
	"drugId"	INTEGER NOT NULL,
	"quantity"	INTEGER DEFAULT 1,
	"dosage"	TEXT DEFAULT NULL,
	"remarks"	TEXT DEFAULT NULL,
	"deletedAt"	TEXT DEFAULT NULL,
	PRIMARY KEY("id"),
	FOREIGN KEY("drugId") REFERENCES "outpatient"("id") ON UPDATE CASCADE ON DELETE CASCADE
);