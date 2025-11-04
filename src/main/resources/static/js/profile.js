const addFilmForm = document.getElementById('add-film-form');


if (addFilmForm) {
    addFilmForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const title = document.getElementById('title').value;
        const description = document.getElementById('description').value;

        const filmData = { title, description };

        try {
            const response = await fetch('/api/films', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(filmData)
            });

            if (response.ok) {
                alert('Фільм успішно додано!');
                addFilmForm.reset();
            } else {
                const errorData = await response.json();
                alert('Помилка додавання: ' + (errorData.message || 'Server error'));
            }
        } catch (err) {
            console.error('Fetch error:', err);
            alert('Мережева помилка. Спробуйте пізніше.');
        }
    });
}