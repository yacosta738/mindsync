export const createAFetchMockResponse = (status: number, json: T) => {
  return {
    status: status,
    json: async () => json,
  };
};
