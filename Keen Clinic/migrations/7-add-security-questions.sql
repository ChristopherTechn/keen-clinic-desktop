CREATE TABLE IF NOT EXISTS securityQuestions(
    "id" integer,
    "userId" integer,
    "mothersMaidenName" TEXT NOT NULL,
    "cityMetSpouse" TEXT NOT NULL,
    "favoriteSport" TEXT NOT NULL,
    PRIMARY KEY("id"),
    FOREIGN KEY("userId") REFERENCES "user"("id") ON UPDATE CASCADE ON DELETE CASCADE
);