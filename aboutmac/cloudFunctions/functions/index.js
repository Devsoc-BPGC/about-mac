const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
const database = admin.database();

exports.androidApps = functions.https.onRequest((request, response) => {
    return database.ref('/androidApps/').once('value').then((snapshot) => {
        return response.send(snapshot.val());
    });
});
exports.postHolders = functions.https.onRequest((request, response) => {
    return database.ref('/postHolders/').once('value').then((snapshot) => {
    	return response.send(snapshot.val());
    });
});
exports.socialLinks = functions.https.onRequest((request, response) => {
    return database.ref('/socialLinks/').once('value').then((snapshot) => {
    	return response.send(snapshot.val());
    });
});
