# Android App

## Location
./Androidproject/app

## Open
Use Android Studio: Open the root Androidproject directory.

## Base URL
Retrofit should use:
```
http://10.0.2.2:8080
```
(10.0.2.2 loops back to host from emulator.)

## Physical Device
Replace with LAN IP of dev machine:
```
http://<your-host-LAN-ip>:8080/api/
```

## Auth
- Relies on cookies; ensure OkHttp client shares a CookieJar singleton.

## Common Actions
- Mark spam: add "spam" + POST /blacklist each URL.
- Move to bin: adjust labels locally + PATCH (ensure backend persists labels).
- Add/remove user labels: POST/DELETE /labels/mails/:mailId with JSON body.

## Troubleshooting
If ConnectException:
- Backend not bound to 0.0.0.0
- Using wrong base URL
- Docker not running
See [Troubleshooting](Troubleshooting.md).