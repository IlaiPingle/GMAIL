const net = require('net');
const PORT = 8080;
const HOST = 'localhost';


function fetchData(endpoint) {
    return new Promise((resolve, reject) => {
        const client = new net.Socket();
        let response = '';
        client.connect(PORT, HOST, () => {
            client.write(`GET ${endpoint}\n`);
        });

        client.on("data", (data) => {
            response += data.toString();
            if (response.endsWith('\n')) {
                client.end();
            }
        });
        client.on('end', () => {
            resolve(response.trim());
        });
        client.on('error', (err) => {
            reject(err);
        });
    });
}