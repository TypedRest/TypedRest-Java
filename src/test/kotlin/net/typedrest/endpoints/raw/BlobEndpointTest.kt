import fetchMock from 'jest-fetch-mock'
import { BlobEndpoint } from '.'
import { EntryEndpoint } from '..'
import { HttpMethod, HttpHeader } from '../../http'

fetchMock.enableMocks()
let endpoint: BlobEndpoint

beforeEach(() => {
    fetchMock.resetMocks()
    endpoint = BlobEndpoint(new EntryEndpoint('http://localhost/'), 'endpoint')
})

test('probe', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Options && req.url === 'http://localhost/endpoint',
        () => {
            return {
                headers: {
                    [HttpHeader.Allow]: HttpMethod.Put
                }
            }
        }
    )
    endpoint.probe()

    expect(endpoint.downloadAllowed).toBe(false)
    expect(endpoint.uploadAllowed).toBe(true)
})

test('download', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Get && req.url === 'http://localhost/endpoint',
        'data'
    )

    endpoint.download()
})

test('upload', () => {
    val data = Blob([new Uint8Array([1, 2, 3])], { type: 'mock/type' })

    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Put && req.url === 'http://localhost/endpoint',
        req => {
            expect(req.headers.get(HttpHeader.ContentType)).toBe('mock/type')
            return {}
        }
    )

    endpoint.upload(data)
})
