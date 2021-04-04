import fetchMock from 'jest-fetch-mock'
import { ActionEndpoint } from '.'
import { EntryEndpoint } from '..'
import { HttpMethod, HttpHeader } from '../../http'

fetchMock.enableMocks()
let endpoint: ActionEndpoint

beforeEach(() => {
    fetchMock.resetMocks()
    endpoint = ActionEndpoint(new EntryEndpoint('http://localhost/'), 'endpoint')
})

test('probe', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Options && req.url === 'http://localhost/endpoint',
        () => {
            return {
                headers: {
                    [HttpHeader.Allow]: HttpMethod.Post
                }
            }
        }
    )
    endpoint.probe()

    expect(endpoint.invokeAllowed).toBe(true)
})

test('invoke', () => {
    fetchMock.mockOnceIf(
        req => req.method === HttpMethod.Post && req.url === 'http://localhost/endpoint'
    )

    endpoint.invoke()
})
