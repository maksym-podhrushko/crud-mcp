module.exports = {
  root: true,
  env: {
    node: true,
  },
  extends: ["plugin:vue/vue3-essential", "eslint:recommended"],
  parserOptions: {
    ecmaVersion: 2020,
    requireConfigFile: false, // Disable Babel config file checking
  },
  rules: {
    // your rules here
  },
};
