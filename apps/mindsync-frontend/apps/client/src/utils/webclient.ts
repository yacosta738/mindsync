// eslint-disable-next-line import/no-extraneous-dependencies
import Cookies from 'js-cookie'

export interface WebClient <T, U> {
    get: (url: string) => Promise<T>;
    post: (url: string, body: U) => Promise<T>;
    put: (url: string, body: U) => Promise<T>;
    delete: (url: string) => Promise<T>;
}

export class FetchWebClient <T, U> implements WebClient<T, U> {
    private buildHeaders(
        xrsfToken: string = Cookies.get('XSRF-TOKEN') || '',
        contentType: string = 'application/json',
    ): Headers {
        const headers = new Headers();
        headers.append('Content-Type', contentType);
        headers.append('X-XSRF-TOKEN', xrsfToken);
        headers.append('Access-Control-Allow-Origin', 'http://localhost:5173');
        return headers;
    }

    public async get(url: string): Promise<T> {
        const headers = this.buildHeaders();
        const response = await fetch(url, {
            method: 'GET',
            headers,
        });
        return response.json();
    }

    public async post(url: string, body: U): Promise<T> {
        const headers = this.buildHeaders();
        const response = await fetch(url, {
            method: 'POST',
            headers,
            body: JSON.stringify(body),
        });
        return response.json();
    }

    public async put(url: string, body: U): Promise<T> {
        const headers = this.buildHeaders();
        const response = await fetch(url, {
            method: 'PUT',
            headers,
            body: JSON.stringify(body),
        });
        return response.json();
    }

    public async delete(url: string): Promise<T> {
        const headers = this.buildHeaders();
        const response = await fetch(url, {
            headers,
            method: 'DELETE',
        });
        return response.json();
    }
}
