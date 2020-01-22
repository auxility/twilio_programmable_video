import * as functions    from 'firebase-functions';
import {CallableContext} from 'firebase-functions/lib/providers/https';

/*
Title: Complete a Room
Twilio NodeJS Example: https://www.twilio.com/docs/video/api/rooms-resource?code-sample=code-complete-a-room&code-language=Node.js&code-sdk-version=3.x
*/

module.exports = async (data: any, context: CallableContext) => {
    // If you plan to use Firebase Authentication, you could do some checks on the context.auth, like this:
    // if (!(context.auth && context.auth.token)) {
    //     throw new functions.https.HttpsError(
    //         'permission-denied',
    //         'Must be an authorized user to execute this function.'
    //     );
    // }

    if (!data.sid) {
        throw new functions.https.HttpsError('invalid-argument', 'sid is required!');
    }

    try {
        const accountSid = functions.config().twilio.live.account_sid;
        const authToken = functions.config().twilio.live.auth_token;

        const client = require('twilio')(accountSid, authToken);

        console.log('Request: ', JSON.stringify(data));
        const response = JSON.stringify(await client.video.rooms(data.sid).update({status: 'completed'}));
        console.log('Response: ', response);
        return JSON.parse(response);
    } catch (e) {
        console.error(e);
        throw new functions.https.HttpsError(
            'aborted',
            `${e}`
        );
    }
};