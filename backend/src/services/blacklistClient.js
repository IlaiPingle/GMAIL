const net = require('net');
const { endianness } = require('os');
const PORT = 4000;
const HOST = 'bloom-server'; // while running in docker, this should be the name of the bloom server container

function sendCommand(command, url) {
	return new Promise((resolve, reject) => {
		const client = new net.Socket();
		client.setTimeout(5000); // Set timeout to 5 seconds
		let response = '';
		client.connect(PORT, HOST, () => {
			client.write(`${command} ${url}\n`);
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

		client.on('timeout', () => {
			client.destroy();
			reject(new Error('Request timed out'));
		});

		client.on('error', (err) => {
			reject(err);
		});
	});
}
module.exports = { sendCommand };
