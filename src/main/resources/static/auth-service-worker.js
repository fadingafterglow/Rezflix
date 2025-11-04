let token = null;

self.addEventListener('message', (event) => {
    if (event.data && event.data.type === 'SET_TOKEN') {
        token = event.data.token;
    }
});

self.addEventListener('fetch', (event) => {
    const { request: originalRequest } = event;
    const url = new URL(originalRequest.url);

    if (url.origin !== self.location.origin) {
        return;
    }

    if (token) {
        const headers = new Headers(originalRequest.headers);
        headers.set('Authorization', `Bearer ${token}`);

        const modifiedRequest = new Request(originalRequest, { headers });
        event.respondWith(fetch(modifiedRequest));
    } else {
        event.respondWith(fetch(originalRequest));
    }
});
