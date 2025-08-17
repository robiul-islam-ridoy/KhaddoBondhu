const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.sendNotification = functions.https.onCall(async (data, context) => {
    // Check if user is authenticated
    if (!context.auth) {
        throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }

    const { targetUserId, title, body, type, targetId } = data;

    try {
        // Get the target user's FCM token
        const userDoc = await admin.firestore().collection('users').doc(targetUserId).get();
        
        if (!userDoc.exists) {
            throw new functions.https.HttpsError('not-found', 'Target user not found');
        }

        const fcmToken = userDoc.data().fcmToken;
        
        if (!fcmToken) {
            throw new functions.https.HttpsError('failed-precondition', 'User has no FCM token');
        }

        // Create the notification message
        const message = {
            token: fcmToken,
            notification: {
                title: title,
                body: body
            },
            data: {
                type: type,
                target_id: targetId,
                click_action: 'FLUTTER_NOTIFICATION_CLICK'
            },
            android: {
                notification: {
                    sound: 'default',
                    channel_id: 'food_requests'
                }
            },
            apns: {
                payload: {
                    aps: {
                        sound: 'default'
                    }
                }
            }
        };

        // Send the notification
        const response = await admin.messaging().send(message);
        
        return { success: true, messageId: response };
        
    } catch (error) {
        console.error('Error sending notification:', error);
        throw new functions.https.HttpsError('internal', 'Failed to send notification');
    }
});

exports.sendNotificationToUser = functions.firestore
    .document('notifications/{notificationId}')
    .onCreate(async (snap, context) => {
        const notification = snap.data();
        
        try {
            // Get the target user's FCM token
            const userDoc = await admin.firestore().collection('users').doc(notification.userId).get();
            
            if (!userDoc.exists) {
                console.log('Target user not found:', notification.userId);
                return;
            }

            const fcmToken = userDoc.data().fcmToken;
            
            if (!fcmToken) {
                console.log('User has no FCM token:', notification.userId);
                return;
            }

            // Create the notification message
            const message = {
                token: fcmToken,
                notification: {
                    title: notification.title,
                    body: notification.message
                },
                data: {
                    type: notification.type,
                    target_id: notification.relatedId || '',
                    notification_id: notification.id,
                    click_action: 'FLUTTER_NOTIFICATION_CLICK'
                },
                android: {
                    notification: {
                        sound: 'default',
                        channel_id: 'food_requests',
                        priority: 'high'
                    }
                },
                apns: {
                    payload: {
                        aps: {
                            sound: 'default',
                            badge: 1
                        }
                    }
                }
            };

            // Send the notification
            const response = await admin.messaging().send(message);
            console.log('Notification sent successfully:', response);
            
        } catch (error) {
            console.error('Error sending notification:', error);
        }
    });
