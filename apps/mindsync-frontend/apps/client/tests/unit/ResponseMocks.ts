export const createAFetchMockResponse = <T>(status: number, json: T) => {
  return {
    status: status,
    json: async () => json,
  };
};
