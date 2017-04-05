import chai from 'chai';
import sinon from 'sinon';
import sinonChai from 'sinon-chai';
import * as Logger from '../logger';

const expect = chai.expect;
//noinspection JSCheckFunctionSignatures
chai.use(sinonChai);

/*global console, describe, it, beforeEach, afterEach*/
describe('Logger', () => {
  beforeEach(() => {
    sinon.stub(console, 'log');
  });

  afterEach(() => {
    console.log.restore();
  });

  describe('setLevel()', () => {
    it('should set logging level', () => {
      const level = Logger.LEVEL.WARN;
      expect(Logger.setLevel(level)).to.equal(level);
    });
    it('should throw TypeError, if argument is not numeric', () => {
      //noinspection JSCheckFunctionSignatures
      expect(() => Logger.setLevel('XXX')).to.throw(TypeError);
    });
  });
  describe('setPrefix()', () => {
    it('should set prefix', () => {
      const prefix = '[LoggerTest]';
      expect(Logger.setPrefix(prefix)).to.equal(prefix);
    });
    it('should throw TypeError, if argument is not a string', () => {
      //noinspection JSCheckFunctionSignatures
      expect(() => Logger.setPrefix(123)).to.throw(TypeError);
    });
  });
  describe('getPrefix()', () => {
    it('should return prefix', () => {
      const prefix = '[LoggerTest]';
      Logger.setPrefix(prefix);
      expect(Logger.getPrefix()).to.equal(prefix);
    });
  });
  describe('log()', () => {
    it('should print a log message to the console', () => {
      const msg = 'This is a log message.';
      Logger.setLevel(Logger.LEVEL.LOG);
      Logger.log(msg);
      // Logger.setLevel() executes a console.log!
      expect(console.log).to.have.been.calledTwice;
      expect(console.log).to.have.been.calledWith(Logger.getPrefix(), msg);
    });
    it('should not print a log message to the console, if logging level doesn´t match', () => {
      const msg = 'This is a log message.';
      Logger.setLevel(Logger.LEVEL.INFO);
      Logger.log(msg);
      // Logger.setLevel() executes a console.log!
      expect(console.log).to.have.been.calledOnce;
      expect(console.log).to.have.not.been.calledWith(Logger.getPrefix(), msg);
    });
  });
  describe('info()', () => {
    beforeEach(() => {
      sinon.stub(console, 'info');
    });

    afterEach(() => {
      console.info.restore();
    });

    it('should print an info message to the console', () => {
      const msg = 'This is an info message.';
      Logger.setLevel(Logger.LEVEL.INFO);
      Logger.info(msg);
      expect(console.info).to.have.been.calledOnce;
      expect(console.info).to.have.been.calledWith(Logger.getPrefix(), msg);
    });
    it('should not print an info message to the console, if logging level doesn´t match', () => {
      const msg = 'This is an info message.';
      Logger.setLevel(Logger.LEVEL.WARN);
      Logger.info(msg);
      expect(console.info).to.have.not.been.called;
    });
  });
  describe('warn()', () => {
    beforeEach(() => {
      sinon.stub(console, 'warn');
    });

    afterEach(() => {
      console.warn.restore();
    });

    it('should print an warning message to the console', () => {
      const msg = 'This is a warning message.';
      Logger.setLevel(Logger.LEVEL.WARN);
      Logger.warn(msg);
      expect(console.warn).to.have.been.calledOnce;
      expect(console.warn).to.have.been.calledWith(Logger.getPrefix(), msg);
    });
    it('should not print a warning message to the console, if logging level doesn´t match', () => {
      const msg = 'This is a warning message.';
      Logger.setLevel(Logger.LEVEL.ERROR);
      Logger.warn(msg);
      expect(console.warn).to.have.not.been.called;
    });
  });
  describe('error()', () => {
    beforeEach(() => {
      sinon.stub(console, 'error');
    });

    afterEach(() => {
      console.error.restore();
    });

    it('should print an error message to the console', () => {
      const msg = 'This is an error message.';
      Logger.setLevel(Logger.LEVEL.ERROR);
      Logger.error(msg);
      expect(console.error).to.have.been.calledOnce;
      expect(console.error).to.have.been.calledWith(Logger.getPrefix(), msg);
    });
    it('should not print an error message to the console, if logging level doesn´t match', () => {
      const msg = 'This is an error message.';
      Logger.setLevel(Logger.LEVEL.OFF);
      Logger.error(msg);
      expect(console.error).to.have.not.been.called;
    });
  });
});