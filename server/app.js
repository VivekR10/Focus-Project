import express from 'express';
import bodyParser from 'body-parser';

export const app = express();

app.set('port', process.env.PORT || 8080);

app.use(bodyParser.json());

app.use('/api', require('./routes/email'));


