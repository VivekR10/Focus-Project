require('babel-register');

const { app } = require('./app.js');

const port = app.get('port');

app.listen(port, () => {
  console.log(`Listening on: ${port}`)
});
