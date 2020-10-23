function serializeObject(form) {
    const obj = {};
    const formData = new FormData(form);
    for (let key of formData.keys()) {
        const data = formData.get(key);
        if (!obj[key]) {
            obj[key] = data;
        } else if (Array.isArray(obj[key])) {
            obj[key].push(data);
        } else {
            const temp = obj[key];
            obj[key] = [];
            obj[key].push(temp, data);
        }
    }
    return obj;
}

function serialize(form) {
    const obj = serializeObject(form);
    return JSON.stringify(obj);
}

function loadLoggedInAccount(callback) {
    const token = sessionStorage.getItem('token');
    fetch('/api/v1/account/me', {
        method: 'get',
        headers: {
            'Authorization': token
        }
    })
    .then(response => response.json())
    .then(callback)
    .catch(console.log);
}