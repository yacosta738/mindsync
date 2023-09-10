export const createAFetchMockResponse = <T>(
  status: number,
  json: T,
  headers: Headers = new Headers()
) => {
  return {
    status: status,
    json: async () => json,
    headers: headers,
  };
};
