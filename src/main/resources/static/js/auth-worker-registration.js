if ('serviceWorker' in navigator) {
    window.addEventListener('load', async () => {
        try {
            const reg = await navigator.serviceWorker.register('/auth-service-worker.js');

            const token = localStorage.getItem('jwt');

            if (token && reg.active) {
                reg.active.postMessage({ type: 'SET_TOKEN', token });
            }
        } catch (err) {
            console.error('Failed to register auth interceptor:', err);
        }
    });
}

function setToken(token) {
    if (navigator.serviceWorker?.controller) {
        navigator.serviceWorker.controller.postMessage({
            type: 'SET_TOKEN',
            token: token
        });
    }
}