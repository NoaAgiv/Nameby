const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// imports firebase-admin module
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.pushNotification = functions.database.ref('/messages/{id}').onWrite( event => {
   console.log('Push notification event triggered');

   /* Grab the current value of what was written to the Realtime Database */
    var valueObject = event.data.val();

    const payload = {
        notification: {
            title: valueObject.title,
            body: valueObject.message,
            sound: "default"
        }
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
    };

   console.log('delivering and deleting message ' + event.params.id);
   admin.database().ref('/messages/'+ event.params.id).remove();

   return admin.messaging().sendToTopic(valueObject.topic, payload, options);

});