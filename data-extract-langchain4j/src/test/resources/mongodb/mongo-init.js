db = new Mongo().getDB("transcripts_db");

db.createCollection('transcripts_collection',{
    capped: true,
    size: 1000000000,
    max: 1000
});

db.transcripts_collection.insertMany([
    {
        id: 1,
        text: "Sample of text for item with id 1",
    },
    {
        id: 2,
        text: "Sample of text for item with id 2",
    },
    {
        id: 3,
        text: "Sample of text for item with id 3",
    }
]);
