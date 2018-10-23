import nodemailer from 'nodemailer';

const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: 'team9.focus@gmail.com',
    pass: 'team9focus',
  },
});

const createMailOptions = (email, body, type) => {

  if (type === 'stats') {
    return createMailOptionsStats(email, body);
  } else if (type === 'badges') {
    return createMailOptionsBadges(email, body);
  }
}

const createMailOptionsStats = (email, body) => {
  const { apps } = body;
  let blockedNotificationsString = '';
  for (const app in apps) {
    const { blockCount, launchBlockCount, minutesBlocked } = apps[app];
    blockedNotificationsString +=
      `App: ${app}
        Notifications Blocked: ${blockCount}
        Launch Attempts Blocked: ${launchBlockCount}
        Minutes blocked: ${minutesBlocked}
      `
  }

  return {
    from: '"Team 9 - Focus" <team9@focus.com>',
    to: email,
    subject: 'Your Weekly Statistics',
    text:
    ` Congratulations on making it through the week!

      ${blockedNotificationsString}
    `,
  };
}

const createMailOptionsBadges = (email, body) => {
  const { dateEarned, badgeName, blockCount, imageURL } = body;
  if (imageURL) {
    /// todo
  }
  return {
    from: '"Team 9 - Focus" <team9@focus.com>',
    to: email,
    subject: 'Badge Earned!',
    text:
    `
    Congratulations on earning ${badgeName}!
    Date Earned: ${dateEarned}
    Number of times blocked: ${blockCount}
    `,
  };
}


const sendMail = (user, body, type) => {
  const options = createMailOptions(user, body, type);
  transporter.sendMail(options, (err, info) => {
    if (err) {
      throw err;
    }
    console.log('Message %s sent: %s', info.messageId, info.response);
  });
}


export default sendMail;

