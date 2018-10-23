import express from 'express';
import { check, validationResult } from 'express-validator/check';
import { matchedData } from 'express-validator/filter';
import sendMail from '../utils/mailer';

const router = express.Router();


router.post('/email/weekly', [
    check('email').isEmail(),
    check('apps').exists(),
  ], (req, res) => {

    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(422).json({ errors: errors.mapped() });
    }

    try {
      sendMail(req.body.email, matchedData(req), 'stats');
    } catch (e) {
      return res.status(400).json({ errors: "Error Processing Email"});
    }

    return res.status(200).send(req.body);
});

router.post('/email/badges', [
    check('dateEarned').exists(),
    check('badgeName').exists(),
    check('blockCount').exists()
  ], (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(422).json({ errors: errors.mapped() });
    }

    try {
      sendMail(req.body.email, matchedData(req), 'badges');
    } catch(e) {
      return res.status(400).json({ errors: "Error Processing Email "});
    }

    return res.status(200).send(req.body);
});

/*
dateEarned
badgeName:
blockCount:
imageURL
*/

module.exports = router;
