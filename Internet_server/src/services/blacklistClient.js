const net = require('net');
const { endianness } = require('os');
const PORT = 4000;

function sendCommand(command, url) {
    return new Promise((resolve, reject) => {
        const client = new net.Socket();
        let response = '';
        client.connect(PORT, 'localhost', () => {
            client.write(`${command} ${url}\n`);
        });
        
        client.on("data", (data) => {
            response += data.toString();
            if (response.endsWith('\n')) {
                client.end();
            }
        });
        client.on('end',() => {
            resolve(response.trim());
        });
        client.on('error', (err) => {
            reject(err);
        });
    });
}
module.exports = { sendCommand };
