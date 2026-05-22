DROP TABLE IF EXISTS DeckCards;
DROP TABLE IF EXISTS Collections;
DROP TABLE IF EXISTS Decks;
DROP TABLE IF EXISTS Cards;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Credentials;


CREATE TABLE Credentials (
                             CredentialsId BIGINT AUTO_INCREMENT PRIMARY KEY,
                             Email VARCHAR(100) NOT NULL UNIQUE,
                             PasswordHash VARCHAR(255) NOT NULL,
                             UserRole VARCHAR(50) NOT NULL DEFAULT 'MEMBER'
);

create table Users (
                      UserId BIGINT AUTO_INCREMENT primary key,
                      Name varchar(100),
                      Age date not null,
                      UserCredentialsId BIGINT NOT NULL unique,
                      FOREIGN KEY (UserCredentialsId) REFERENCES Credentials(CredentialsId)
);
create table Cards (
                       CardId BIGINT AUTO_INCREMENT PRIMARY KEY,
                       CharacterName VARCHAR(100),
                       CardType VARCHAR(50),
                       Color VARCHAR(50),
                       CardSet VARCHAR(100),
                       Rarity VARCHAR(50),
                       RuleText VARCHAR(1800),
                       PictureReference VARCHAR(500),
                       ManaCost VARCHAR(20),
                       ATK INT,
                       DEF INT
);

create table Collections (
                           collectionId BIGINT auto_increment primary key,
                           UserId bigint not null,
                           CardId BIGINT Not null,
                           foreign key (UserId) references Users(UserId),
                           foreign key (CardId) references Cards(CardId)
);


create table Decks (
                    DeckId BIGINT auto_increment primary key,
                    DeckName varchar(255) not null,
                    Format Varchar(50),
                    UserId BIGINT not null,
                    foreign key (UserId) references Users(UserId)
);

create table DeckCards (
                          DeckCardId bigint primary key,
                          DeckId bigint,
                          CardId bigInt,
                          foreign key(DeckId) references Decks(DeckId),
                          foreign key(CardId) references Cards(CardId)
);
