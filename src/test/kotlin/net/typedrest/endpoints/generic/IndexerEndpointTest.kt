import { IndexerEndpoint, ElementEndpoint } from '.'
import { EntryEndpoint } from '..'

let endpoint: IndexerEndpoint<ElementEndpoint<{}>>

beforeEach(() => {
    endpoint = IndexerEndpoint<ElementEndpoint<{}>>(new EntryEndpoint('http://localhost/'), 'endpoint', ElementEndpoint)
})

test('getById', () => {
    expect(endpoint.get('x/y').uri).toEqual(new URI.create('http://localhost/endpoint/x%2Fy'))
})
